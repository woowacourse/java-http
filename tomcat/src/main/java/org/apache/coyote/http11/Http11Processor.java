package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final HttpRequest request = HttpRequestParser.parse(reader);
            if (request == null) {
                return;
            }

            final SessionContext session = createSessionContext(request);
            if (isLoggedInRequest(request) && isLoggedIn(session.id)) {
                writeRedirect(outputStream, session, "/index.html");
                return;
            }
            if (isPost(request, "/login")) {
                handleLogin(outputStream, request.getBody(), session);
                return;
            }
            if (isPost(request, "/register")) {
                handleRegister(outputStream, request.getBody(), session);
                return;
            }

            final byte[] responseBody = loadResource(request.getPath());
            writeOk(outputStream, responseBody, request.getPath(), session);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private SessionContext createSessionContext(final HttpRequest request) {
        final String cookieHeader = request.getHeader("Cookie");
        HttpCookie cookie = null;
        if (cookieHeader != null) {
            cookie = new HttpCookie(cookieHeader);
        }
        String cookieSessionId = null;
        if (cookie != null) {
            cookieSessionId = cookie.getJsessionId();
        }
        if (cookieSessionId != null) {
            return new SessionContext(cookieSessionId, false);
        }
        return new SessionContext(UUID.randomUUID().toString(), true);
    }

    private boolean isLoggedInRequest(final HttpRequest request) {
        return "/login".equals(request.getPath()) && "GET".equals(request.getMethod());
    }

    private boolean isPost(final HttpRequest request, final String path) {
        return path.equals(request.getPath()) && "POST".equals(request.getMethod());
    }

    private void handleLogin(final OutputStream outputStream, final String body,
                             final SessionContext session) throws IOException {
        final Map<String, String> parameters = parseForm(body);
        final User user = InMemoryUserRepository.findByAccount(parameters.get("account"))
                .filter(foundUser -> foundUser.checkPassword(parameters.get("password")))
                .orElse(null);
        if (user == null) {
            writeRedirect(outputStream, session, "/401.html");
            return;
        }

        HttpSession httpSession = SESSION_MANAGER.findSession(session.id);
        if (httpSession == null) {
            if (!session.shouldSetCookie) {
                session.id = UUID.randomUUID().toString();
                session.shouldSetCookie = true;
            }
            httpSession = new Session(session.id);
            SESSION_MANAGER.add(httpSession);
        }
        httpSession.setAttribute("user", user);
        writeRedirect(outputStream, session, "/index.html");
    }

    private void handleRegister(final OutputStream outputStream, final String body,
                                final SessionContext session) throws IOException {
        final Map<String, String> parameters = parseForm(body);
        final User user = new User(
                parameters.get("account"),
                parameters.get("password"),
                parameters.get("email")
        );
        InMemoryUserRepository.save(user);
        writeRedirect(outputStream, session, "/index.html");
    }

    private Map<String, String> parseForm(final String body) {
        final Map<String, String> parameters = new HashMap<>();
        for (final String parameter : body.split("&")) {
            final String[] pair = parameter.split("=", 2);
            if (pair.length == 2) {
                parameters.put(pair[0], URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }
        return parameters;
    }

    private byte[] loadResource(final String path) throws IOException {
        if ("/".equals(path)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }
        final String resourcePath = switch (path) {
            case "/login" -> "/login.html";
            case "/register" -> "/register.html";
            default -> path;
        };
        try (InputStream resourceStream = getClass().getClassLoader()
                .getResourceAsStream("static" + resourcePath)) {
            if (resourceStream == null) {
                throw new IllegalArgumentException("리소스를 찾을 수 없습니다: static" + resourcePath);
            }
            return resourceStream.readAllBytes();
        }
    }

    private void writeRedirect(final OutputStream outputStream, final SessionContext session,
                               final String location) throws IOException {
        final StringBuilder response = new StringBuilder()
                .append("HTTP/1.1 302 Found\r\n");
        appendSetCookieIfMissing(response, session);
        response.append("Location: ").append(location).append("\r\n")
                .append("Content-Length: 0\r\n\r\n");
        outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private void writeOk(final OutputStream outputStream, final byte[] body, final String path,
                         final SessionContext session) throws IOException {
        final StringBuilder response = new StringBuilder()
                .append("HTTP/1.1 200 OK \r\n");
        appendSetCookieIfMissing(response, session);
        response.append("Content-Type: ").append(getContentType(path)).append("\r\n")
                .append("Content-Length: ").append(body.length).append(" \r\n\r\n");
        outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8 ";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }

    private boolean isLoggedIn(final String jSessionId) throws IOException {
        final HttpSession session = SESSION_MANAGER.findSession(jSessionId);
        return session != null && session.getAttribute("user") != null;
    }

    private void appendSetCookieIfMissing(final StringBuilder response, final SessionContext session) {
        if (session.shouldSetCookie) {
            response.append("Set-Cookie: JSESSIONID=").append(session.id).append("\r\n");
        }
    }

    private static final class SessionContext {
        private String id;
        private boolean shouldSetCookie;

        private SessionContext(final String id, final boolean shouldSetCookie) {
            this.id = id;
            this.shouldSetCookie = shouldSetCookie;
        }
    }
}
