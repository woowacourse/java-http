package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            HttpRequest request = HttpRequest.from(inputStream);
            if (request == null) {
                return;
            }
            String sessionId = request.getCookie("JSESSIONID");

            String setCookieHeader = "";
            if (sessionId == null) {
                sessionId = UUID.randomUUID().toString();
                setCookieHeader = "Set-Cookie: JSESSIONID=" + sessionId + "\r\n";
            }

            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.findSession(sessionId);

            if (session == null) {
                session = new Session(sessionId);
                sessionManager.add(session);
            }

            String path = request.getPath();
            String redirectLocation = handleLogin(request, session);
            if (redirectLocation == null) {
                redirectLocation = handleRegister(request);
            }

            if (redirectLocation != null) {
                final var response =
                        "HTTP/1.1 302 Found\r\n"
                                + "Location: " + redirectLocation + "\r\n"
                                + setCookieHeader
                                + "Content-Length: 0\r\n"
                                + "\r\n";

                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            byte[] responseBody = readResponseBody(path);
            String contentType = resolveContentType(path);

            final var response =
                    "HTTP/1.1 200 OK\r\n"
                            + setCookieHeader
                            + "Content-Type: " + contentType + "charset=utf-8\r\n"
                            + "Content-Length: " + responseBody.length + "\r\n"
                            + "\r\n";

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String handleLogin(HttpRequest request, Session session) {
        if (!"/login".equals(request.getPath())) {
            return null;
        }

        User loginUser = (User) session.getAttribute("user");
        if (loginUser != null) {
            return "/index.html";
        }

        if (!"POST".equals(request.getMethod()) || request.getBody().isEmpty()) {
            return null;
        }

        String account = request.getParameter("account");
        String password = request.getParameter("password");

        if (account == null || password == null) {
            return "/401.html";
        }

        Optional<User> foundUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));

        if (foundUser.isEmpty()) {
            return "/401.html";
        }

        User user = foundUser.get();
        session.setAttribute("user", user);

        return "/index.html";
    }

    private String handleRegister(HttpRequest request) {
        if (!"POST".equals(request.getMethod()) || !"/register".equals(request.getPath()) || request.getBody().isEmpty()) {
            return null;
        }

        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        if (account == null || password == null || email == null) {
            return null;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        return "/index.html";
    }

    private String resolveContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;";
        }

        return "text/html;";
    }

    private byte[] readResponseBody(String requestUri) throws IOException {
        if ("/".equals(requestUri)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        if ("/login".equals(requestUri) || "/register".equals(requestUri)) {
            requestUri = requestUri + ".html";
        }

        String resourceName = "static" + requestUri;
        try (InputStream resourceStream =
                     Http11Processor.class
                             .getClassLoader()
                             .getResourceAsStream(resourceName)) {

            return Objects.requireNonNull(resourceStream).readAllBytes();
        }
    }

}
