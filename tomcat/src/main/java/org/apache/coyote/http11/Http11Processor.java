package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int MAX_LINE_LENGTH = 8192;

    private final Socket connection;
    private final Manager manager;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.manager = SessionManager.getInstance();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final var httpRequest = parseRequest(inputStream);
            final var response = dispatch(httpRequest);
            outputStream.write(response.getResponseBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Request parseRequest(final InputStream inputStream) throws IOException {
        final String requestLineString = readLine(inputStream);
        if (requestLineString.isBlank()) {
            return Http11Request.createInvalid();
        }
        final var requestLine = RequestLine.from(requestLineString);
        final var headers = HttpHeaders.from(inputStream);
        final String body = parseBody(inputStream, headers);
        return new Http11Request(
                requestLine,
                headers,
                body
        );
    }

    private String readLine(final InputStream inputStream) throws IOException {
        final var buffer = new ByteArrayOutputStream();
        int nextByte;
        while ((nextByte = inputStream.read()) != -1) {
            if (buffer.size() >= MAX_LINE_LENGTH) {
                throw new IOException("요청 라인/헤더가 최대 길이 " + MAX_LINE_LENGTH + "를 초과합니다.");
            }
            if (nextByte == '\n') {
                break;
            }
            if (nextByte == '\r') {
                inputStream.read();
                break;
            }
            buffer.write(nextByte);
        }
        return buffer.toString(StandardCharsets.US_ASCII);
    }

    private String parseBody(
            final InputStream inputStream,
            final HttpHeaders headers
    ) throws IOException {
        final int contentLength = headers.getContentLength();
        if (contentLength == 0) {
            return "";
        }
        final var bodyBytes = inputStream.readNBytes(contentLength);
        return new String(bodyBytes, StandardCharsets.UTF_8);
    }

    private Http11Response dispatch(final Http11Request httpRequest) {
        return getResponse(httpRequest);
    }

    private Http11Response getResponse(final Http11Request httpRequest) {
        final var path = httpRequest.getPath();
        if ("/".equals(path)) {
            return new Http11Response(200, "text/html;charset=utf-8", "Hello world!");
        }
        if ("/login".equals(path)) {
            return handleLoginRequest(httpRequest);
        }
        if ("/register".equals(path)) {
            return handleRegisterRequest(httpRequest);
        }
        return serveStaticFile(path);
    }

    private Http11Response handleLoginRequest(final Http11Request httpRequest) {
        if (httpRequest.isPost()) {
            final var params = extractFirstParamValues(RequestLine.parseUrlEncodedParams(httpRequest.getBody()));
            final Optional<User> userOptional = isLoginSuccessful(params);
            if (userOptional.isPresent()) {
                final var user = userOptional.get();
                final var session = getSession(httpRequest, true)
                        .orElseThrow(() -> new IllegalStateException("세션 생성에 실패했습니다."));
                session.setAttribute("user", user);
                final var response = Http11Response.redirect("/index.html");
                response.addCookie("JSESSIONID", session.getId());
                return response;
            }
            return Http11Response.redirect("/401.html");
        }
        final Optional<Session> sessionOptional = getSession(httpRequest, false);
        if (sessionOptional.isPresent() && sessionOptional.get().getAttribute("user") != null) {
            return Http11Response.redirect("/index.html");
        }
        return serveStaticFile("/login.html");
    }

    private Optional<Session> getSession(
            final Http11Request request,
            final boolean create
    ) {
        final Optional<String> jSessionId = request.getCookies()
                .getCookie("JSESSIONID")
                .map(HttpCookie::getValue);
        return jSessionId.flatMap(manager::findSession)
                .or(() -> {
                    if (create) {
                        return Optional.of(createNewSession());
                    }
                    return Optional.empty();
                });
    }

    private Session createNewSession() {
        final var newSession = new Session(UUID.randomUUID().toString());
        manager.add(newSession);
        return newSession;
    }

    private Optional<User> isLoginSuccessful(final Map<String, String> params) {
        if (!params.containsKey("account") || !params.containsKey("password")) {
            return Optional.empty();
        }
        final String account = params.get("account");
        final String password = params.get("password");
        final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }
        final var user = userOptional.get();
        if (!user.checkPassword(password)) {
            return Optional.empty();
        }
        log.info("login success: {}", user);
        return Optional.of(user);
    }

    private Http11Response handleRegisterRequest(final Http11Request httpRequest) {
        if (httpRequest.isPost()) {
            final var params = extractFirstParamValues(RequestLine.parseUrlEncodedParams(httpRequest.getBody()));
            final var user = new User(
                    params.get("account"),
                    params.get("password"),
                    params.get("email")
            );
            InMemoryUserRepository.save(user);
            log.info("user created: {}", user);
            return Http11Response.redirect("/index.html");
        }
        return serveStaticFile("/register.html");
    }

    private Map<String, String> extractFirstParamValues(final Map<String, List<String>> params) {
        final Map<String, String> result = new HashMap<>();
        for (var entry : params.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                result.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        return result;
    }

    private Http11Response serveStaticFile(final String path) {
        return readStaticResource(path)
                .map(body -> new Http11Response(200, getContentType(path), body))
                .orElseGet(this::serveNotFoundPage);
    }

    private Http11Response serveNotFoundPage() {
        return readStaticResource("/404.html")
                .map(body -> new Http11Response(404, "text/html;charset=utf-8", body))
                .orElse(new Http11Response(404, "text/html;charset=utf-8", "404 Not Found"));
    }

    private Optional<byte[]> readStaticResource(final String path) {
        final String resourcePath = "static" + path;
        try (final InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                return Optional.empty();
            }
            return Optional.of(resourceStream.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
