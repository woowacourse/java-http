package org.apache.coyote.http11;

import com.sun.net.httpserver.Request;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String SESSION_USER = "user";

    private final Socket connection;

    private final StaticResourceLoader resourceLoader = new StaticResourceLoader();
    private final SessionManager sessionManager = SessionManager.getInstance();

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            HttpRequest httpRequest = new HttpRequest(reader);

            HttpCookie cookies = new HttpCookie(httpRequest.getHeader("cookie"));
            Optional<String> newSessionId = cookies.createJSessionIdIfAbsent();
            String sessionId = cookies.get(HttpCookie.JSESSION_ID).orElseThrow();

            handleRequest(httpRequest, sessionId, newSessionId, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }



    private void handleRequest(
            HttpRequest request,
            String sessionId,
            Optional<String> newSessionId,
            OutputStream outputStream
    ) throws IOException {
        String path = request.getRequestUri().getPath();
        switch (path) {
            case "/" -> writeResponse(outputStream, "200 OK", "Hello world!", "text/html", newSessionId);
            case "/register" -> handleRegister(request, newSessionId, outputStream);
            case "/login" -> handleLogin(request, sessionId, newSessionId, outputStream);
            case "/session" -> handleSession(sessionId, newSessionId, outputStream);
            case "/logout" -> handleLogout(request, sessionId, newSessionId, outputStream);
            default -> serveResource(path, newSessionId, outputStream);
        }
    }

    private void handleRegister(
            HttpRequest request,
            Optional<String> newSessionId,
            OutputStream outputStream
    ) throws IOException {
        switch (request.getMethod()) {
            case HttpMethod.GET -> serveResource("/register.html", newSessionId, outputStream);
            case HttpMethod.POST -> {
                Map<String, String> parameters = parseFormBody(request.getBody());
                InMemoryUserRepository.save(new User(
                        parameters.get("account"),
                        parameters.get("password"),
                        parameters.get("email")
                ));

                writeRedirect(outputStream, "/index.html", newSessionId);
            }
            default -> writeResponse(outputStream, "405 Method Not Allowed", "Method Not Allowed", "text/plain", newSessionId);
        }
    }

    private void handleLogin(
            HttpRequest request,
            String sessionId,
            Optional<String> newSessionId,
            OutputStream outputStream
    ) throws IOException {
        switch (request.getMethod()) {
            case HttpMethod.GET -> {
                if (getLoginUser(sessionId).isPresent()) {
                    writeRedirect(outputStream, "/index.html", newSessionId);
                    return;
                }
                serveResource("/login.html", newSessionId, outputStream);
            }
            case HttpMethod.POST -> {
                Map<String, String> parameters = parseFormBody(request.getBody());
                Optional<User> loginUser = login(parameters);
                if (loginUser.isEmpty()) {
                    writeRedirect(outputStream, "/401.html", newSessionId);
                    return;
                }

                User user = loginUser.get();
                log.info("회원 조회 성공: account={}", user.getAccount());
                Session session = getOrCreateSession(sessionId);
                session.setAttribute(SESSION_USER, user);
                Optional<String> responseSessionId = newSessionId;
                if (!session.getId().equals(sessionId)) {
                    responseSessionId = Optional.of(session.getId());
                }
                writeRedirect(outputStream, "/index.html", responseSessionId);
            }
            default -> writeResponse(outputStream, "405 Method Not Allowed", "Method Not Allowed", "text/plain", newSessionId);
        }
    }

    private Session getOrCreateSession(String sessionId) {
        Session session = sessionManager.findSession(sessionId);
        if (session != null) {
            return session;
        }

        Session newSession = new Session(UUID.randomUUID().toString());
        sessionManager.add(newSession);
        return newSession;
    }

    private Optional<User> getLoginUser(String sessionId) {
        Session session = sessionManager.findSession(sessionId);
        if (session == null) {
            return Optional.empty();
        }

        Object user = session.getAttribute(SESSION_USER);
        if (user instanceof User loginUser) {
            return Optional.of(loginUser);
        }
        return Optional.empty();
    }

    private void handleSession(
            String sessionId,
            Optional<String> newSessionId,
            OutputStream outputStream
    ) throws IOException {
        Optional<User> loginUser = getLoginUser(sessionId);
        String responseBody = loginUser
                .map(user -> "{\"loggedIn\":true,\"account\":\"" + escapeJson(user.getAccount()) + "\"}")
                .orElse("{\"loggedIn\":false}");
        writeResponse(outputStream, "200 OK", responseBody, "application/json", newSessionId);
    }

    private String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder();
        for (char character : value.toCharArray()) {
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        return escaped.toString();
    }

    private void handleLogout(
            HttpRequest request,
            String sessionId,
            Optional<String> newSessionId,
            OutputStream outputStream
    ) throws IOException {
        switch (request.getMethod()) {
            case HttpMethod.POST -> {
                Session session = sessionManager.findSession(sessionId);
                if (session != null) {
                    session.invalidate();
                }

                writeResponse(outputStream, "204 No Content", "", "text/plain", newSessionId);
            }
            default -> writeResponse(outputStream, "405 Method Not Allowed", "Method Not Allowed", "text/plain", newSessionId);
        }
    }

    private Map<String, String> parseFormBody(String body) {
        Map<String, String> parameters = new HashMap<>();

        for (String parameter : body.split("&")) {
            String[] pair = parameter.split("=", 2);
            if (pair.length != 2) {
                continue;
            }

            String name = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            parameters.put(name, value);
        }

        return parameters;
    }

    private void serveResource(
            String path,
            Optional<String> newSessionId,
            OutputStream outputStream
    ) throws IOException {
        String responseBody;
        try {
            responseBody = resourceLoader.load(path);
        } catch (FileNotFoundException e) {
            writeResponse(outputStream, "404 Not Found", "Not Found", "text/plain", newSessionId);
            return;
        }

        String contentType = path.endsWith(".css") ? "text/css" : "text/html";
        writeResponse(outputStream, "200 OK", responseBody, contentType, newSessionId);
    }

    private Optional<User> login(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private void writeRedirect(
            OutputStream outputStream,
            String location,
            Optional<String> newSessionId
    ) throws IOException {
        String response = "HTTP/1.1 302 Found\r\n"
                + setCookieHeader(newSessionId)
                + "Location: " + location + "\r\n"
                + "Content-Length: 0\r\n\r\n";
        write(outputStream, response);
    }

    private void writeResponse(
            OutputStream outputStream,
            String status,
            String responseBody,
            String contentType,
            Optional<String> newSessionId
    ) throws IOException {
        byte[] responseBodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);

        String response = "HTTP/1.1 " + status + " \r\n"
                + setCookieHeader(newSessionId)
                + "Content-Type: " + contentType + ";charset=utf-8 \r\n"
                + "Content-Length: " + responseBodyBytes.length + " \r\n"
                + "\r\n"
                + responseBody;
        write(outputStream, response);
    }

    private void write(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String setCookieHeader(Optional<String> newSessionId) {
        return newSessionId
                .map(sessionId -> "Set-Cookie: " + HttpCookie.JSESSION_ID + "=" + sessionId + "\r\n")
                .orElse("");
    }
}
