package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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

        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            HttpRequest request = HttpRequest.parse(inputStream);
            HttpResponse response = handleRequest(request);

            response.writeTo(outputStream);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(HttpRequest request) throws IOException {
        String method = request.requestLine().method();
        String path = request.requestLine().path();

        Optional<Cookie> sessionCookie = request.headers().getCookie("JSESSIONID");

        if ("GET".equals(method)) {
            return handleGetRequest(path, sessionCookie);
        }

        if ("POST".equals(method)) {
            return handlePostRequest(path, request.body());
        }

        return emptyResponse(405, "Method Not Allowed");
    }

    private HttpResponse handleGetRequest(String resourcePath, Optional<Cookie> sessionCookie) throws IOException {
        Session session = findSession(sessionCookie);

        if ("/login".equals(resourcePath) && isLoggedIn(session)) {
            return generateRedirectResponse("/index.html");
        }

        return serveStaticResource(resourcePath);
    }

    private HttpResponse handlePostRequest(String resourcePath, RequestBody body) {
        Map<String, String> formData = body.parseFormData();

        if (resourcePath.equals("/register")) {
            return handleRegister(formData);
        }

        if (resourcePath.equals("/login")) {
            return handleLogin(formData);
        }

        return emptyResponse(405, "Method Not Allowed");
    }

    private Session findSession(Optional<Cookie> sessionCookie) {
        if (sessionCookie.isEmpty()) {
            return null;
        }

        return SessionManager.findSession(sessionCookie.get().value());
    }

    private boolean isLoggedIn(Session session) {
        return session != null && session.getAttribute("user") != null;
    }

    private HttpResponse handleRegister(Map<String, String> formData) {
        String account = formData.get("account");
        String email = formData.get("email");
        String password = formData.get("password");

        InMemoryUserRepository.save(new User(account, password, email));

        return generateRedirectResponse("/index.html");
    }

    private HttpResponse handleLogin(Map<String, String> formData) {
        String account = formData.get("account");
        String password = formData.get("password");

        Optional<User> authenticatedUser = authenticate(account, password);

        if (authenticatedUser.isEmpty()) {
            return generateRedirectResponse("/401.html");
        }

        Session session = createSession(authenticatedUser);

        return generateRedirectResponse("/index.html", session.getId());
    }

    @Nonnull
    private static Session createSession(Optional<User> authenticatedUser) {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        session.setAttribute("user", authenticatedUser.get());
        SessionManager.add(session);

        return session;
    }

    private HttpResponse generateRedirectResponse(String location) {
        return new HttpResponse(
                new StatusLine("HTTP/1.1", 302, "Found"),
                Map.of("Location", location),
                new byte[0]
        );
    }

    private HttpResponse generateRedirectResponse(String location, String sessionId) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Location", location);
        headers.put("Set-Cookie", "JSESSIONID=" + sessionId);

        return new HttpResponse(
                new StatusLine("HTTP/1.1", 302, "Found"),
                headers,
                new byte[0]
        );
    }

    private Optional<User> authenticate(String account, String password) {
        if (account == null || account.isBlank()) {
            return Optional.empty();
        }

        if (password == null || password.isBlank()) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private HttpResponse serveStaticResource(String resourcePath) throws IOException {
        byte[] bytes = resolveResponseBody(resourcePath);
        String contentType = resolveContentType(resourcePath);

        return new HttpResponse(
                new StatusLine("HTTP/1.1", 200, "OK"),
                Map.of("Content-Type", contentType),
                bytes
        );
    }

    private String resolveContentType(String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        if (resourcePath.endsWith(".js")) {
            return "text/javascript;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }

    private byte[] resolveResponseBody(String resourcePath) throws IOException {
        if (resourcePath.equals("/")) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        if (resourcePath.equals("/login") || resourcePath.equals("/register")) {
            return readResource("static" + resourcePath + ".html");
        }

        return readResource("static" + resourcePath);
    }

    private byte[] readResource(String resourcePath) throws IOException {
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                throw new IOException(resourcePath + " 파일을 찾을 수 없습니다.");
            }

            return resourceStream.readAllBytes();
        }
    }

    private HttpResponse emptyResponse(int statusCode, String reasonPhrase) {
        return new HttpResponse(
                new StatusLine("HTTP/1.1", statusCode, reasonPhrase),
                Map.of(),
                new byte[0]
        );
    }
}
