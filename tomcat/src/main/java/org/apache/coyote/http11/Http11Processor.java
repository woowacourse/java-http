package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    /**
     * 단일 요청 처리 진입점
     *
     * @param connection
     */
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

            final HttpRequest request = new HttpRequest(inputStream);
            final String method = request.getMethod().name();
            final String requestUri = request.getPath();
            final String cookieHeader = request.getHeader("cookie");
            final String requestSessionId = Cookie.getValue(cookieHeader, "JSESSIONID");
            final Session existingSession = SessionManager.findSession(requestSessionId);
            final Session session = existingSession != null
                    ? existingSession
                    : SessionManager.createSession();
            final String sessionCookie = existingSession == null
                    ? "JSESSIONID=" + session.getId()
                    : null;

            final String responseBody;
            if ("/".equals(requestUri)) {
                responseBody = "Hello world!";
            } else {
                final String resourcePath;

                if ("/login".equals(requestUri)) {
                    if ("GET".equalsIgnoreCase(method)
                            && session.getAttribute("user") != null) {
                        writeRedirectResponse(outputStream, "/index.html", sessionCookie);
                        return;
                    }

                    if ("POST".equalsIgnoreCase(method)) {
                        final Map<String, String> queryParams = request.getParameters();

                        final String account = queryParams.get("account");
                        final String password = queryParams.get("password");

                        final Optional<User> optionalUser = account == null
                                ? Optional.empty()
                                : InMemoryUserRepository.findByAccount(account);
                        final boolean loginSuccess = password != null
                                && optionalUser
                                .map(user -> user.checkPassword(password))
                                .orElse(false);

                        if (loginSuccess) {
                            final User user = optionalUser.orElseThrow();
                            session.setAttribute("user", user);
                            log.info("회원 조회 결과: {}", user);
                        }

                        writeRedirectResponse(
                                outputStream,
                                loginSuccess ? "/index.html" : "/401.html",
                                sessionCookie
                        );
                        return;
                    }

                    resourcePath = "/login.html";
                } else if ("/register".equals(requestUri)) {
                    if ("POST".equalsIgnoreCase(method)) {
                        final Map<String, String> formData = request.getParameters();
                        final String account = formData.get("account");
                        final String password = formData.get("password");
                        final String email = formData.get("email");

                        if (account != null && password != null && email != null) {
                            final User user = new User(account, password, email);
                            InMemoryUserRepository.save(user);
                            log.info("회원가입 결과: {}", user);
                        }

                        writeRedirectResponse(outputStream, "/index.html", sessionCookie);
                        return;
                    }

                    resourcePath = "/register.html";
                } else {
                    resourcePath = requestUri;
                }

                final var resource = getClass().getClassLoader()
                        .getResource("static" + resourcePath);

                if (resource == null) {
                    return;
                }

                try {
                    final byte[] body =
                            Files.readAllBytes(Path.of(resource.toURI()));

                    responseBody = new String(body, StandardCharsets.UTF_8);
                } catch (URISyntaxException e) {
                    throw new IllegalStateException(e);
                }
            }

            final String contentType;
            if (requestUri.endsWith(".css")) {
                contentType = "text/css";
            } else {
                contentType = "text/html;charset=utf-8";
            }

            final List<String> responseHeaders = new ArrayList<>();
            responseHeaders.add("HTTP/1.1 200 OK ");

            if (sessionCookie != null) {
                responseHeaders.add("Set-Cookie: " + sessionCookie + " ");
            }

            responseHeaders.add("Content-Type: " + contentType + " ");
            responseHeaders.add("Content-Length: "
                    + responseBody.getBytes(StandardCharsets.UTF_8).length + " ");
            responseHeaders.add("");
            responseHeaders.add(responseBody);

            final String response = String.join("\r\n", responseHeaders);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeRedirectResponse(final OutputStream outputStream,
                                       final String location,
                                       final String sessionCookie) throws IOException {
        final List<String> responseHeaders = new ArrayList<>();
        responseHeaders.add("HTTP/1.1 302 Found");

        if (sessionCookie != null) {
            responseHeaders.add("Set-Cookie: " + sessionCookie);
        }

        responseHeaders.add("Location: " + location);
        responseHeaders.add("Content-Length: 0");
        responseHeaders.add("");
        responseHeaders.add("");

        final String response = String.join("\r\n", responseHeaders);

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
