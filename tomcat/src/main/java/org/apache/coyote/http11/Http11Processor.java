package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String NOT_FOUND_PAGE = "/404.html";

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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }
            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String uri = parts[1];
            String path = uri;
            int index = uri.indexOf("?");
            if (index != -1) {
                path = uri.substring(0, index);
            }
            Map<String, String> headers = readHeaders(reader);
            String requestBody = readRequestBody(reader, headers);
            HttpCookie cookie = new HttpCookie(headers.get("Cookie"));
            String sessionId = cookie.get("JSESSIONID");
            String response = createResponse(method, path, requestBody, sessionId);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] keyValue = line.split(":", 2);
            if (keyValue.length == 2) {
                headers.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return headers;
    }

    private String readRequestBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        if (!headers.containsKey("Content-Length")) {
            return "";
        }
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int readCount = reader.read(buffer, totalRead, contentLength - totalRead);
            if (readCount == -1) {
                break;
            }
            totalRead += readCount;
        }
        return new String(buffer, 0, totalRead);
    }

    private String createResponse(String method, String path, String requestBody, String sessionId) throws IOException {
        String setCookie = null;
        if (sessionId == null) {
            setCookie = "JSESSIONID=" + UUID.randomUUID();
        }
        if ("POST".equals(method) && "/login".equals(path)) {
            return login(requestBody, setCookie);
        }
        if ("POST".equals(method) && "/register".equals(path)) {
            return register(requestBody, setCookie);
        }
        if ("GET".equals(method) && "/login".equals(path) && isLoggedIn(sessionId)) {
            return buildRedirectResponse(INDEX_PAGE, setCookie);
        }
        if ("/".equals(path)) {
            return buildResponse("HTTP/1.1 200 OK ", getContentType(path), "Hello world!", setCookie);
        }
        String resourcePath = path;
        if ("/login".equals(path)) {
            resourcePath = "/login.html";
        }
        if ("/register".equals(path)) {
            resourcePath = REGISTER_PAGE;
        }
        String responseBody = readStaticResource(resourcePath);
        if (responseBody == null) {
            String notFoundBody = readStaticResource(NOT_FOUND_PAGE);
            return buildResponse("HTTP/1.1 404 Not Found ", getContentType(NOT_FOUND_PAGE), notFoundBody, setCookie);
        }
        return buildResponse("HTTP/1.1 200 OK ", getContentType(resourcePath), responseBody, setCookie);
    }

    private String buildRedirectResponse(String location, String setCookie) {
        List<String> lines = new ArrayList<>();
        lines.add("HTTP/1.1 302 Found ");
        lines.add("Location: " + location + " ");
        if (setCookie != null) {
            lines.add("Set-Cookie: " + setCookie + " ");
        }
        lines.add("");
        lines.add("");
        return String.join("\r\n", lines);
    }

    private String buildResponse(String statusLine, String contentType, String responseBody, String setCookie) {
        List<String> lines = new ArrayList<>();
        lines.add(statusLine);
        lines.add("Content-Type: " + contentType + " ");
        lines.add("Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ");
        if (setCookie != null) {
            lines.add("Set-Cookie: " + setCookie + " ");
        }
        lines.add("");
        lines.add(responseBody);
        return String.join("\r\n", lines);
    }

    private String readStaticResource(String path) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("static" + path)) {
            if (resource == null) {
                return null;
            }
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private String login(String requestBody, String setCookie) {
        Map<String, String> parameters = parseQueryString(requestBody);
        String account = parameters.get("account");
        String password = parameters.get("password");
        if (account == null || password == null) {
            return buildRedirectResponse(UNAUTHORIZED_PAGE, setCookie);
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return buildRedirectResponse(UNAUTHORIZED_PAGE, setCookie);
        }
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user.get());
        SessionManager.add(session);
        log.info("user : {}", user.get().getAccount());
        return buildRedirectResponse(INDEX_PAGE, "JSESSIONID=" + session.getId());
    }

    private boolean isLoggedIn(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        Session session = SessionManager.findSession(sessionId);
        return session != null && session.getAttribute("user") != null;
    }

    private String register(String requestBody, String setCookie) {
        Map<String, String> parameters = parseQueryString(requestBody);
        String account = parameters.get("account");
        String password = parameters.get("password");
        String email = parameters.get("email");
        if (account == null || account.isBlank()
                || password == null || password.isBlank()
                || email == null || email.isBlank()) {
            return buildRedirectResponse(REGISTER_PAGE, setCookie);
        }
        InMemoryUserRepository.save(new User(account, password, email));
        return buildRedirectResponse(INDEX_PAGE, setCookie);
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }
        return queryParams;
    }
}
