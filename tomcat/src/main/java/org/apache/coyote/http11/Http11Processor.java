package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String NOT_FOUND_RESOURCE_PATH = "static/404.html";
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String USER_SESSION_ATTRIBUTE = "user";
    private static final Manager SESSION_MANAGER = SessionManager.getInstance();

    private final Socket connection;

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
        try (final var inputStream = new BufferedInputStream(connection.getInputStream());
            final var outputStream = connection.getOutputStream()) {
            final var request = HttpRequestParser.parse(inputStream);
            handleRequest(request, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequest(final HttpRequest request, final OutputStream outputStream) throws IOException {
        if (request.isPost() && "/register".equals(request.path())) {
            try {
                register(readForm(request));
                writeResponse(outputStream, "302 Found", Map.of("Location", "/index.html"), new byte[0]);
            } catch (final IllegalArgumentException e) {
                final String failedLocation = "/register.html?error=" + URLEncoder.encode(e.getMessage(), UTF_8);
                writeResponse(outputStream, "302 Found", Map.of("Location", failedLocation), new byte[0]);
            }
        } else if (request.isPost() && "/login".equals(request.path())) {
            try {
                final User user = authenticate(readForm(request));
                final String sessionId = addAuthSession(request.headers().cookie(), user);
                writeResponse(outputStream, "302 Found", loginSuccessHeaders(sessionId), new byte[0]);
            } catch (final IllegalArgumentException e) {
                final String invalidRedirectUri = "/login.html?error=" + URLEncoder.encode(e.getMessage(), UTF_8);
                writeResponse(outputStream, "302 Found", Map.of("Location", invalidRedirectUri), new byte[0]);
            }
        } else if ("/".equals(request.path())) {
            final var body = "Hello world!".getBytes(UTF_8);
            writeResponse(outputStream, "200 OK", contentTypeHeader("text/html"), body);
        } else {
            if ("/login".equals(request.path()) && isLoggedIn(request.headers().cookie())) {
                writeResponse(outputStream, "302 Found", Map.of("Location", "/index.html"), new byte[0]);
                return;
            }
            writeResource(outputStream, "200 OK", STATIC_RESOURCE_PATH + appendHtmlExtension(request.path()));
        }
    }

    private Map<String, String> readForm(final HttpRequest request) {
        if (!request.headers().isFormUrlEncoded()) {
            return Map.of();
        }
        return FormUrlEncoded.parse(new String(request.body(), UTF_8));
    }

    private void register(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");
        final String email = params.get("email");
        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("계정 정보가 비어있다.");
        }
        if (InMemoryUserRepository.existsByAccount(account)) {
            throw new IllegalArgumentException("계정이 존재한다.");
        }
        InMemoryUserRepository.save(new User(account, password, email));
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String status,
            final Map<String, String> headers,
            final byte[] responseBody
    ) throws IOException {
        final var responseHeader = new StringBuilder().append("HTTP/1.1 %s \r\n".formatted(status));
        headers.forEach((name, value) -> responseHeader.append("%s: %s \r\n".formatted(name, value)));
        responseHeader.append("Content-Length: %d \r\n".formatted(responseBody.length)).append("\r\n");

        outputStream.write(responseHeader.toString().getBytes(UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private User authenticate(final Map<String, String> params) {
        final String account = params.get("account");
        if (account == null) {
            throw new IllegalArgumentException("로그인 정보가 잘못됐다.");
        }
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(params.get("password")))
                .orElseThrow(() -> new IllegalArgumentException("로그인 정보가 잘못됐다."));
    }

    private static String addAuthSession(
            final HttpCookie cookie,
            final User user
    ) throws IOException {
        final String sessionId = cookie.get(SESSION_COOKIE_NAME);
        final HttpSession previousSession = SESSION_MANAGER.findSession(sessionId);
        if (previousSession != null) {
            SESSION_MANAGER.remove(previousSession);
        }

        final HttpSession newSession = new Session(UUID.randomUUID().toString());
        newSession.setAttribute(USER_SESSION_ATTRIBUTE, user);
        SESSION_MANAGER.add(newSession);
        return newSession.getId();
    }

    private Map<String, String> loginSuccessHeaders(final String sessionId) {
        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", "/index.html");
        headers.put("Set-Cookie", SESSION_COOKIE_NAME + "=" + sessionId);
        return headers;
    }

    private Map<String, String> contentTypeHeader(final String contentType) {
        if (contentType.startsWith("text/")) {
            return Map.of("Content-Type", contentType + ";charset=utf-8");
        }
        return Map.of("Content-Type", contentType);
    }

    private boolean isLoggedIn(final HttpCookie cookie) throws IOException {
        final String sessionId = cookie.get(SESSION_COOKIE_NAME);
        final HttpSession session = SESSION_MANAGER.findSession(sessionId);
        return nonNull(session) && nonNull(session.getAttribute(USER_SESSION_ATTRIBUTE));
    }

    private void writeResource(
            final OutputStream outputStream,
            final String status,
            final String resourcePath
    ) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                writeResource(outputStream, "404 Not Found", NOT_FOUND_RESOURCE_PATH);
                return;
            }

            final var contentType = MimeTypeResolver.resolve(resourcePath);
            writeResponse(outputStream, status, contentTypeHeader(contentType), resource.readAllBytes());
        }
    }

    private static String appendHtmlExtension(final String resourcePath) {
        final var fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        if (fileName.contains(".")) {
            return resourcePath;
        }
        return resourcePath + ".html";
    }
}
