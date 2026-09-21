package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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

            final String requestLine = readLine(inputStream);
            if (requestLine == null) {
                return;
            }

            final String method = extractMethod(requestLine);
            final String requestTarget = extractRequestTarget(requestLine);
            final String path = extractPath(requestTarget);
            final Map<String, String> headers = readHeaders(inputStream);
            final Map<String, String> parameters = parseQueryParameters(requestTarget);
            if ("POST".equals(method)) {
                parameters.putAll(parseFormBody(inputStream, headers));
            }

            final Map<String, String> cookies = parseCookies(headers.get("cookie"));
            final String requestedSessionId = cookies.get("JSESSIONID");
            final String sessionId = requestedSessionId == null
                    ? UUID.randomUUID().toString()
                    : requestedSessionId;
            final Session session = SessionManager.getOrCreate(sessionId);
            final String sessionIdToSet = requestedSessionId == null ? sessionId : null;

            final byte[] responseBody = handleRequest(
                    outputStream, method, path, parameters, session, sessionIdToSet
            );
            writeResponse(outputStream, path, responseBody, sessionIdToSet);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readLine(final InputStream inputStream) throws IOException {
        final var buffer = new ByteArrayOutputStream();
        int value;
        while ((value = inputStream.read()) != -1) {
            if (value == '\n') {
                break;
            }
            if (value != '\r') {
                buffer.write(value);
            }
        }

        if (value == -1 && buffer.size() == 0) {
            return null;
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private String extractMethod(final String requestLine) {
        return parseRequestLine(requestLine)[0].toUpperCase(Locale.ROOT);
    }

    private String extractRequestTarget(final String requestLine) {
        return parseRequestLine(requestLine)[1];
    }

    private String[] parseRequestLine(final String requestLine) {
        final String[] requestLineParts = requestLine.trim().split("\\s+", 3);

        if (requestLineParts.length != 3) {
            throw new IllegalArgumentException(
                    "올바르지 않은 HTTP 요청 라인입니다: " + requestLine
            );
        }
        return requestLineParts;
    }

    private String extractPath(final String requestTarget) {
        return requestTarget.split("\\?", 2)[0];
    }

    private Map<String, String> parseQueryParameters(final String requestTarget) {
        final String[] targetParts = requestTarget.split("\\?", 2);
        final String queryString = targetParts.length == 2 ? targetParts[1] : "";

        return parseQueryString(queryString);
    }

    private Map<String, String> readHeaders(final InputStream inputStream) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = readLine(inputStream)) != null && !line.isEmpty()) {
            final String[] header = line.split(":", 2);
            if (header.length == 2) {
                headers.put(header[0].trim().toLowerCase(Locale.ROOT), header[1].trim());
            }
        }
        return headers;
    }

    private Map<String, String> parseFormBody(final InputStream inputStream,
                                               final Map<String, String> headers) throws IOException {
        final String contentType = headers.getOrDefault("content-type", "");
        if (!contentType.toLowerCase(Locale.ROOT)
                .startsWith("application/x-www-form-urlencoded")) {
            return new HashMap<>();
        }

        final int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        final byte[] body = inputStream.readNBytes(contentLength);
        return parseQueryString(new String(body, StandardCharsets.UTF_8));
    }

    private Map<String, String> parseCookies(final String cookieHeader) {
        final Map<String, String> cookies = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return cookies;
        }

        for (final String cookie : cookieHeader.split(";")) {
            final String[] pair = cookie.trim().split("=", 2);
            if (pair.length == 2) {
                cookies.put(pair[0].trim(), pair[1].trim());
            }
        }
        return cookies;
    }

    private void writeResponse(final OutputStream outputStream, final String path,
                               final byte[] responseBody,
                               final String sessionIdToSet) throws IOException {
        if (responseBody == null) {
            return;
        }

        outputStream.write(createResponseHeader(path, responseBody.length, sessionIdToSet));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private byte[] createRedirectResponseHeader(final String location,
                                                final String sessionIdToSet) {
        final var responseHeader = new StringBuilder()
                .append("HTTP/1.1 302 Found\r\n")
                .append("Location: ").append(location).append("\r\n")
                .append("Content-Length: 0\r\n");
        appendSessionCookie(responseHeader, sessionIdToSet);
        responseHeader.append("\r\n");
        return responseHeader.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void writeRedirectResponse(final OutputStream outputStream,
                                       final String location,
                                       final String sessionIdToSet) throws IOException {
        outputStream.write(createRedirectResponseHeader(location, sessionIdToSet));
        outputStream.flush();
    }

    private byte[] createResponseHeader(final String path, final int contentLength,
                                        final String sessionIdToSet) {
        final var responseHeader = new StringBuilder()
                .append("HTTP/1.1 200 OK\r\n")
                .append("Content-Type: ").append(resolveContentType(path))
                .append(";charset=utf-8\r\n")
                .append("Content-Length: ").append(contentLength).append("\r\n");
        appendSessionCookie(responseHeader, sessionIdToSet);
        responseHeader.append("\r\n");
        return responseHeader.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void appendSessionCookie(final StringBuilder responseHeader,
                                     final String sessionIdToSet) {
        if (sessionIdToSet != null) {
            responseHeader.append("Set-Cookie: JSESSIONID=")
                    .append(sessionIdToSet)
                    .append("\r\n");
        }
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }

        return "text/html";
    }

    private byte[] handleRequest(final OutputStream outputStream,
                                 final String method,
                                 final String path,
                                 final Map<String, String> parameters,
                                 final Session session,
                                 final String sessionIdToSet) throws IOException {
        if ("/".equals(path)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        if ("/login".equals(path)) {
            if ("GET".equals(method) && session.getAttribute("user") != null) {
                writeRedirectResponse(outputStream, "/index.html", sessionIdToSet);
                return null;
            }

            if ("GET".equals(method) && parameters.isEmpty()) {
                return readResource("/login.html");
            }

            final Optional<User> authenticatedUser = login(parameters);
            authenticatedUser.ifPresent(user -> session.setAttribute("user", user));
            final String location = authenticatedUser.isPresent() ? "/index.html" : "/401.html";
            writeRedirectResponse(outputStream, location, sessionIdToSet);
            return null;
        }

        if ("/register".equals(path)) {
            if ("POST".equals(method)) {
                final String location = register(parameters) ? "/index.html" : "/register";
                writeRedirectResponse(outputStream, location, sessionIdToSet);
                return null;
            }

            return readResource("/register.html");
        }

        return readResource(path);
    }

    private boolean register(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        final String email = parameters.get("email");
        if (isBlank(account) || isBlank(password) || isBlank(email)) {
            return false;
        }
        InMemoryUserRepository.save(new User(account, password, email));
        return true;
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> queryParameters = new HashMap<>();
        if (queryString.isBlank()) {
            return queryParameters;
        }

        for (final String parameter : queryString.split("&")) {
            final String[] pair = parameter.split("=", 2);
            if (pair.length == 2) {
                final String name = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                queryParameters.put(name, value);
            }
        }
        return queryParameters;
    }

    private Optional<User> login(final Map<String, String> queryParameters) {
        final String account = queryParameters.get("account");
        final String password = queryParameters.get("password");
        if (account == null || password == null) {
            return Optional.empty();
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            User foundUser = user.get();
            if (foundUser.checkPassword(password)) {
                log.info("로그인 사용자 조회 성공: {}", foundUser);
                return user;
            }
            else {
                log.info("아이디 또는 비밀번호가 일치하지 않습니다.");
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static final class SessionManager {

        private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

        private SessionManager() {
        }

        public static void save(final Session session) {
            sessions.put(session.getId(), session);
        }

        public static Optional<Session> findById(final String id) {
            return Optional.ofNullable(sessions.get(id));
        }

        public static Session getOrCreate(final String id) {
            return sessions.computeIfAbsent(id, Session::new);
        }

        public static void remove(final String id) {
            sessions.remove(id);
        }
    }

    public static final class Session {

        private final String id;
        private final Map<String, Object> attributes = new ConcurrentHashMap<>();

        private Session(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public Object getAttribute(final String name) {
            return attributes.get(name);
        }

        public void setAttribute(final String name, final Object value) {
            attributes.put(name, value);
        }

        public void removeAttribute(final String name) {
            attributes.remove(name);
        }

        public void invalidate() {
            attributes.clear();
            SessionManager.remove(id);
        }
    }

    private byte[] readResource(final String uri) throws IOException {
        try (InputStream resource = getClass().getClassLoader()
                .getResourceAsStream("static" + uri)) {
            if (resource == null) {
                return null;
            }

            return resource.readAllBytes();
        }
    }
}
