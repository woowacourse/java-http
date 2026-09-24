package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
            final var reader = new BufferedReader(new InputStreamReader(inputStream));

            String readLine = reader.readLine();
            if (readLine == null) {
                return;
            }

            RequestLine requestLine = RequestLine.parse(readLine);

            Map<String, String> headers = readHeaders(reader);
            String body = readBody(reader, headers);

            Optional<Cookie> sessionCookie = findSessionCookie(headers);
            String response = handleRequest(requestLine.method(), requestLine.target(), body, sessionCookie);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Optional<Cookie> findSessionCookie(Map<String, String> headers) {
        String cookieHeader = headers.get("cookie");

        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return Optional.empty();
        }

        String[] cookiePairs = cookieHeader.split(";");

        for (String pair : cookiePairs) {
            Optional<Cookie> parsed = Cookie.parse(pair);
            if (parsed.isEmpty()) {
                continue;
            }

            String cookieName = parsed.get().name();
            if ("JSESSIONID".equals(cookieName)) {
                return parsed;
            }
        }

        return Optional.empty();
    }

    private String readBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));

        char[] body = new char[contentLength];
        int current = 0;

        while (current < contentLength) {
            int read = reader.read(body, current, contentLength - current);

            if (read == -1) {
                throw new IOException("요청 body가 예상된 값보다 짧습니다.");
            }

            current += read;
        }

        return new String(body);
    }

    private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");

            if (colonIndex == -1) {
                continue;
            }

            String name = line.substring(0, colonIndex)
                    .trim()
                    .toLowerCase();

            String value = line.substring(colonIndex + 1).trim();

            headers.put(name, value);
        }

        return headers;
    }

    private String handleRequest(String method, String target, String body, Optional<Cookie> sessionCookie)
            throws IOException {
        String resourcePath = extractResourcePath(target);

        if ("GET".equals(method)) {
            return handleGetRequest(resourcePath, sessionCookie);
        }

        if ("POST".equals(method)) {
            return handlePostRequest(resourcePath, body);
        }

        return emptyResponse("HTTP/1.1 405 Method Not Allowed");
    }

    private String handleGetRequest(String resourcePath, Optional<Cookie> sessionCookie) throws IOException {
        Session session = findSession(sessionCookie);

        if ("/login".equals(resourcePath) && isLoggedIn(session)) {
            return generateRedirectResponse("/index.html");
        }

        return serveStaticResource(resourcePath);
    }

    private String handlePostRequest(String resourcePath, String body) {
        Map<String, String> formData = parseFormData(body);

        if (resourcePath.equals("/register")) {
            return handleRegister(formData);
        }

        if (resourcePath.equals("/login")) {
            return handleLogin(formData);
        }

        return emptyResponse("HTTP/1.1 405 Method Not Allowed");
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

    private Map<String, String> parseFormData(String body) {
        Map<String, String> formData = new HashMap<>();

        if (body == null || body.isBlank()) {
            return formData;
        }

        String[] parameters = body.split("&");

        for (String parameter : parameters) {
            String[] values = parameter.split("=", 2);

            if (values.length != 2) {
                continue;
            }

            String key = java.net.URLDecoder.decode(values[0], StandardCharsets.UTF_8);
            String value = java.net.URLDecoder.decode(values[1], StandardCharsets.UTF_8);

            formData.put(key, value);
        }

        return formData;
    }

    private String handleRegister(Map<String, String> formData) {
        String account = formData.get("account");
        String email = formData.get("email");
        String password = formData.get("password");

        InMemoryUserRepository.save(new User(account, password, email));

        return generateRedirectResponse("/index.html");
    }

    private String handleLogin(Map<String, String> formData) {
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

    private String generateRedirectResponse(String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "Content-Length: 0",
                "",
                ""
        );
    }

    private String generateRedirectResponse(String location, String sessionId) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "Set-Cookie: JSESSIONID=" + sessionId,
                "Content-Length: 0",
                "",
                ""
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

    private String serveStaticResource(String resourcePath) throws IOException {
        byte[] bytes = resolveResponseBody(resourcePath);
        String responseBody = new String(bytes, StandardCharsets.UTF_8);
        String contentType = resolveContentType(resourcePath);

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: " + contentType,
                "Content-Length: " + bytes.length,
                "",
                responseBody
        );

        return response;
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

    private String extractResourcePath(String path) {
        int index = path.indexOf("?");
        if (index == -1) {
            return path;
        }

        return path.substring(0, index);
    }

    private byte[] readResource(String resourcePath) throws IOException {
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                throw new IOException(resourcePath + " 파일을 찾을 수 없습니다.");
            }

            return resourceStream.readAllBytes();
        }
    }

    private String emptyResponse(String statusLine) {
        return String.join("\r\n",
                statusLine,
                "Content-Length: 0",
                "",
                ""
        );
    }
}
