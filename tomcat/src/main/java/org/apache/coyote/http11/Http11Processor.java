package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final byte[] HELLO_WORLD = "Hello world!".getBytes(StandardCharsets.UTF_8);
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String GET = "GET";
    private static final String POST = "POST";

    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";

    private static final String COOKIE = "Cookie";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER_SESSION_KEY = "user";

    private final Socket connection;
    private static final SessionManager SESSION_MANAGER =
            SessionManager.getInstance();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final BufferedInputStream inputStream =
                     new BufferedInputStream(connection.getInputStream());
             final OutputStream outputStream = connection.getOutputStream()) {

            final Optional<HttpRequest> optionalRequest = HttpRequest.from(inputStream);

            if (optionalRequest.isEmpty()) {
                return;
            }
            final HttpRequest request = optionalRequest.get();

            final Map<String, String> responseHeaders = new LinkedHashMap<>();

            final String sessionId = resolveSessionId(request, responseHeaders);

            if (handleLogin(outputStream, request, sessionId, responseHeaders)) {
                return;
            }

            if (handleRegister(outputStream, request, responseHeaders)) {
                return;
            }

            if ("/".equals(request.getPath())) {
                writeResponse(outputStream, "200 OK",
                        "text/html;charset=utf-8",
                        HELLO_WORLD, responseHeaders);
                return;
            }

            writeStaticResource(outputStream, request.getPath(), responseHeaders);
        } catch (IOException
                 | URISyntaxException
                 | UncheckedServletException e) {

            log.error(e.getMessage(), e);
        }
    }

    private String resolveSessionId(
            final HttpRequest request,
            final Map<String, String> responseHeaders
    ) {

        final Optional<String> existingSessionId = request.getCookie(JSESSIONID);

        if (existingSessionId.isPresent() && !existingSessionId.get().isBlank()) {
            return existingSessionId.get();
        }

        final String newSessionId = UUID.randomUUID().toString();
        responseHeaders.put(SET_COOKIE, JSESSIONID + "=" + newSessionId);
        return newSessionId;
    }

    private boolean handleLogin(
            final OutputStream outputStream,
            final HttpRequest request,
            final String sessionId,
            final Map<String, String> responseHeaders
    ) throws IOException {
        if (!LOGIN_PATH.equals(request.getPath())) {
            return false;
        }

        if (GET.equals(request.getMethod())) {// 이미 로그인한 사용자가 GET /login
            final HttpSession session = SESSION_MANAGER.findSession(sessionId);

            if (session != null && getUser(session) != null) {
                writeRedirect(outputStream, "/index.html", responseHeaders);
                return true;
            }
            // Session이 없거나 로그인하지 않았다면 새로 만들지 않고 login.html을 보여준다.
            return false;
        }

        if (!POST.equals(request.getMethod())) {
            return false;
        }

        final String account = request.getParameter("account").orElse(null);

        final String password = request.getParameter("password").orElse(null);

        if (account == null || password == null) {
            writeRedirect(outputStream, "/401.html", responseHeaders);
            return true;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));

        if (user.isEmpty()) {
            log.info("login failed account: {}", account);
            writeRedirect(outputStream, "/401.html", responseHeaders);
            return true;
        }
        final User loginUser = user.get();

        // 로그인에 성공했을 때에만 Session을 생성한다.
        final HttpSession session = SESSION_MANAGER.createSession();
        // 서버 Session에 로그인 User 저장
        session.setAttribute(USER_SESSION_KEY, loginUser);
        responseHeaders.put(SET_COOKIE, JSESSIONID + "=" + session.getId());
        log.info("login success account: {}", loginUser.getAccount());
        writeRedirect(outputStream, "/index.html", responseHeaders);
        return true;

    }

    private User getUser(final HttpSession session) {
        final Object value = session.getAttribute(USER_SESSION_KEY);
        if (value instanceof User user) {
            return user;
        }
        return null;
    }

    private boolean handleRegister(
            final OutputStream outputStream,
            final HttpRequest request,
            final Map<String, String> responseHeaders
    ) throws IOException {

        if (!REGISTER_PATH.equals(request.getPath())) {
            return false;
        }

        if (GET.equals(request.getMethod())) {
            return false;
        }

        if (!POST.equals(request.getMethod())) {
            return false;
        }

        final String account = request.getParameter("account").orElse(null);

        final String password = request.getParameter("password").orElse(null);

        final String email = request.getParameter("email").orElse(null);

        if (account == null || password == null || email == null) {
            return false;
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("register success account: {}", account);

        writeRedirect(outputStream, "/index.html", responseHeaders);

        return true;
    }


    private void writeRedirect(
            final OutputStream outputStream,
            final String location,
            final Map<String, String> commonHeaders
    ) throws IOException {

        final Map<String, String> responseHeaders =
                new LinkedHashMap<>(
                        commonHeaders
                );
        responseHeaders.put(
                "Location",
                location
        );

        writeResponse(
                outputStream,
                "302 Found",
                null,
                new byte[0],
                responseHeaders
        );
    }

    private void writeStaticResource(
            final OutputStream outputStream,
            final String path,
            final Map<String, String> responseHeaders
    ) throws IOException, URISyntaxException {

        final String resourcePath = resolveResourcePath(path);

        final URL resource = getClass()
                .getClassLoader()
                .getResource(resourcePath);
        if (resource == null) {
            writeNotFound(outputStream, responseHeaders);
            return;
        }
        final byte[] responseBody =
                Files.readAllBytes(
                        Path.of(resource.toURI())
                );


        writeResponse(
                outputStream,
                "200 OK",
                resolveContentType(path),
                responseBody,
                responseHeaders
        );
    }

    private void writeNotFound(
            final OutputStream outputStream,
            final Map<String, String> responseHeaders
    ) throws IOException {

        final byte[] responseBody =
                "Not Found".getBytes(
                        StandardCharsets.UTF_8
                );

        writeResponse(
                outputStream,
                "404 Not Found",
                "text/plain;charset=utf-8",
                responseBody,
                responseHeaders
        );
    }

    private String resolveResourcePath(final String path) {
        if ("/login".equals(path)) {
            return "static/login.html";
        }
        if (REGISTER_PATH.equals(path)) {
            return "static/register.html";
        }

        return "static" + path;
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "application/javascript";
        }

        return "text/html;charset=utf-8";
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String status,
            final String contentType,
            final byte[] responseBody,
            final Map<String, String> additionalHeaders
    ) throws IOException {

        final StringBuilder responseHeaders =
                new StringBuilder();

        responseHeaders
                .append("HTTP/1.1 ")
                .append(status)
                .append(" \r\n");

        if (contentType != null) {
            responseHeaders
                    .append("Content-Type: ")
                    .append(contentType)
                    .append(" \r\n");
        }

        for (Map.Entry<String, String> header
                : additionalHeaders.entrySet()) {

            responseHeaders
                    .append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(" \r\n");
        }

        responseHeaders
                .append("Content-Length: ")
                .append(responseBody.length)
                .append(" \r\n")
                .append("\r\n");

        outputStream.write(
                responseHeaders
                        .toString()
                        .getBytes(
                                StandardCharsets.UTF_8
                        )
        );

        outputStream.write(responseBody);
        outputStream.flush();
    }
}

