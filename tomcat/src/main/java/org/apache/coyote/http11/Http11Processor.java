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
import org.apache.catalina.controller.resource.StaticResourceController;
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
    private final StaticResourceController staticResourceController;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.manager = SessionManager.getInstance();
        this.staticResourceController = new StaticResourceController();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        Http11Response response = new Http11Response();
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final var httpRequest = parseRequest(inputStream);
            dispatch(httpRequest, response);
            outputStream.write(response.getResponseBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.setStatus(500);
            response.setBody("Server Error", "text/plain;charset=utf-8");
            try {
                connection.getOutputStream().write(response.getResponseBytes());
                connection.getOutputStream().flush();
            } catch (IOException ioException) {
                log.error(ioException.getMessage(), ioException);
            }
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

    private void dispatch(final Http11Request httpRequest, final Http11Response httpResponse) throws Exception {
        final var path = httpRequest.getPath();
        if ("/".equals(path)) {
            httpResponse.setBody("Hello world!", "text/html;charset=utf-8");
            return;
        }
        if ("/login".equals(path)) {
            handleLoginRequest(httpRequest, httpResponse);
            return;
        }
        if ("/register".equals(path)) {
            handleRegisterRequest(httpRequest, httpResponse);
            return;
        }
        staticResourceController.service(httpRequest, httpResponse);
    }

    private void handleLoginRequest(final Http11Request httpRequest, final Http11Response httpResponse) {
        if (httpRequest.isPost()) {
            final var params = extractFirstParamValues(RequestLine.parseUrlEncodedParams(httpRequest.getBody()));
            final Optional<User> userOptional = isLoginSuccessful(params);
            if (userOptional.isPresent()) {
                final var user = userOptional.get();
                final var session = getSession(httpRequest, true)
                        .orElseThrow(() -> new IllegalStateException("세션 생성에 실패했습니다."));
                session.setAttribute("user", user);
                httpResponse.setStatus(302);
                httpResponse.setHeader("Location", "/index.html");
                final String cookieValue = String.format("%s=%s; Path=/; HttpOnly; SameSite=Lax", "JSESSIONID", session.getId());
                httpResponse.addHeader("Set-Cookie", cookieValue);
                return;
            }
            httpResponse.setStatus(302);
            httpResponse.setHeader("Location", "/401.html");
            return;
        }
        final Optional<Session> sessionOptional = getSession(httpRequest, false);
        if (sessionOptional.isPresent() && sessionOptional.get().getAttribute("user") != null) {
            httpResponse.setStatus(302);
            httpResponse.setHeader("Location", "/index.html");
            return;
        }
        try {
            staticResourceController.service(httpRequest, httpResponse);
        } catch (Exception e) {
            throw new UncheckedServletException(e);
        }
    }

    private void handleRegisterRequest(final Http11Request httpRequest, final Http11Response httpResponse) {
        if (httpRequest.isPost()) {
            final var params = extractFirstParamValues(RequestLine.parseUrlEncodedParams(httpRequest.getBody()));
            final var user = new User(
                    params.get("account"),
                    params.get("password"),
                    params.get("email")
            );
            InMemoryUserRepository.save(user);
            log.info("user created: {}", user);
            httpResponse.setStatus(302);
            httpResponse.setHeader("Location", "/index.html");
            return;
        }
        try {
            staticResourceController.service(httpRequest, httpResponse);
        } catch (Exception e) {
            throw new UncheckedServletException(e);
        }
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

    private Map<String, String> extractFirstParamValues(final Map<String, List<String>> params) {
        final Map<String, String> result = new HashMap<>();
        for (var entry : params.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                result.put(entry.getKey(), entry.getValue().get(0));
            }
        }
        return result;
    }
}
