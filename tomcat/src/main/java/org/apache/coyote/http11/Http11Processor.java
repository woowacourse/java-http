package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_ROOT = "static";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final String USER_SESSION_KEY = "user";

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

            final var request = new HttpRequest(inputStream);
            final var response = new HttpResponse(outputStream);
            final var session = request.getSession(true);
            final var sessionCookie = createSessionCookie(request.getRequestedSessionId(), session);
            if (sessionCookie != null) {
                response.addCookie(sessionCookie);
            }

            if (isLoginPageRequest(request, session)) {
                response.sendRedirect("/index.html");
                return;
            }

            final var redirectLocation = processForm(request, session);
            if (redirectLocation != null) {
                response.sendRedirect(redirectLocation);
                return;
            }

            final var responseBody = readResponseBody(request.getPath());
            final var contentType = findContentType(request.getPath());

            response.send(contentType, responseBody);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String processForm(
            final HttpRequest request,
            final Session session
    ) {
        if (!"POST".equals(request.getMethod())) {
            return null;
        }

        final var account = request.getParameter("account");
        final var password = request.getParameter("password");

        if ("/register".equals(request.getPath())) {
            final var email = request.getParameter("email");
            InMemoryUserRepository.save(new User(account, password, email));
            return "/index.html";
        }

        if ("/login".equals(request.getPath())) {
            final var user = InMemoryUserRepository.findByAccount(account)
                    .filter(foundUser -> foundUser.checkPassword(password));

            if (user.isPresent()) {
                session.setAttribute(USER_SESSION_KEY, user.get());
                return "/index.html";
            }

            return "/401.html";
        }

        return null;
    }

    private byte[] readResponseBody(final String requestPath) throws IOException {
        if ("/".equals(requestPath)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        validateRequestPath(requestPath);

        final var resourcePath = findResourcePath(requestPath);
        final var classLoader = Http11Processor.class.getClassLoader();

        try (final var resource = classLoader.getResourceAsStream(resourcePath)) {
            if (resource == null) {
                throw new IOException("리소스를 찾을 수 없습니다: " + resourcePath);
            }

            return resource.readAllBytes();
        }
    }

    private String findResourcePath(final String requestPath) {
        if ("/login".equals(requestPath)) {
            return STATIC_ROOT + "/login.html";
        }

        if ("/register".equals(requestPath)) {
            return STATIC_ROOT + "/register.html";
        }

        return STATIC_ROOT + requestPath;
    }

    private void validateRequestPath(final String requestPath) {
        final var pathSegments = List.of(requestPath.split("/"));

        if (!requestPath.startsWith("/") || pathSegments.contains("..")) {
            throw new UncheckedServletException(
                    new IllegalArgumentException("유효하지 않은 요청 경로입니다: " + requestPath)
            );
        }
    }

    private String findContentType(final String requestPath) {
        final var contentType = URLConnection.guessContentTypeFromName(requestPath);

        if ("/".equals(requestPath) || "text/html".equals(contentType)) {
            return HTML_CONTENT_TYPE;
        }

        if (contentType == null) {
            return DEFAULT_CONTENT_TYPE;
        }

        return contentType;
    }

    private String createSessionCookie(final String requestedSessionId, final Session session) {
        if (session.getId().equals(requestedSessionId)) {
            return null;
        }

        return "JSESSIONID=" + session.getId();
    }

    private boolean isLoginPageRequest(
            final HttpRequest request,
            final Session session
    ) {
        return "GET".equals(request.getMethod())
                && "/login".equals(request.getPath())
                && session.getAttribute(USER_SESSION_KEY) != null;
    }

}
