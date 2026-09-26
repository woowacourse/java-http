package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
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
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HTTP_OK = "HTTP/1.1 200 OK ";
    private static final String HTTP_FOUND = "HTTP/1.1 302 Found ";
    private static final String HTTP_BAD_REQUEST = "HTTP/1.1 400 Bad Request ";
    private static final String HTTP_NOT_FOUND = "HTTP/1.1 404 Not Found ";

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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            try {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                final String requestLine = reader.readLine();
                if (requestLine == null) {
                    return;
                }

                final String[] requestParts = parseRequestLine(requestLine);
                final Map<String, String> requestHeaders = readHeaders(reader);
                final String requestBody = readRequestBody(reader, requestHeaders);
                final String method = requestParts[0];
                final String requestPath = extractRequestPath(requestParts[1]);
                final PreparedSession preparedSession = prepareSession(requestHeaders);

                if (isLoginRequest(method, requestPath)) {
                    handleLogin(method, requestBody, preparedSession, outputStream);
                    return;
                }
                if (isRegisterRequest(method, requestPath)) {
                    handleRegister(requestBody, preparedSession, outputStream);
                    return;
                }

                writeResourceResponse(outputStream, requestPath, preparedSession.newSessionId());
            } catch (BadRequestException e) {
                writeBadRequestResponse(outputStream);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String[] parseRequestLine(final String requestLine) {
        final String[] requestParts = requestLine.split(" ");
        if (requestParts.length != 3) {
            throw new BadRequestException();
        }
        return requestParts;
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> requestHeaders = new HashMap<>();
        String headerLine;
        while ((headerLine = reader.readLine()) != null) {
            if (headerLine.isEmpty()) {
                return requestHeaders;
            }

            final String[] headerParts = headerLine.split(":", 2);
            if (headerParts.length != 2) {
                continue;
            }

            final String headerName = headerParts[0].trim().toLowerCase(Locale.ROOT);
            final String headerValue = headerParts[1].trim();
            requestHeaders.put(headerName, headerValue);
        }
        throw new BadRequestException();
    }

    private String readRequestBody(final BufferedReader reader, final Map<String, String> requestHeaders) throws IOException {
        final String contentLengthHeader = requestHeaders.get("content-length");
        if (contentLengthHeader == null) {
            return "";
        }

        final int contentLength;
        try {
            contentLength = Integer.parseInt(contentLengthHeader);
        } catch (NumberFormatException e) {
            throw new BadRequestException(e);
        }

        if (contentLength < 0) {
            throw new BadRequestException();
        }

        final char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            final int readCount = reader.read(buffer, totalRead, contentLength - totalRead);
            if (readCount == -1) {
                throw new BadRequestException();
            }
            totalRead += readCount;
        }
        return new String(buffer);
    }

    private String extractRequestPath(final String requestUri) {
        final int querySeparatorIndex = requestUri.indexOf("?");
        if (querySeparatorIndex == -1) {
            return requestUri;
        }
        return requestUri.substring(0, querySeparatorIndex);
    }

    private Map<String, String> parseParameters(final String requestBody) {
        final Map<String, String> parameters = new HashMap<>();
        if (requestBody.isEmpty()) {
            return parameters;
        }

        for (final String parameter : requestBody.split("&")) {
            final String[] parameterParts = parameter.split("=", 2);
            if (parameterParts.length != 2) {
                continue;
            }

            try {
                final String name = URLDecoder.decode(parameterParts[0], StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(parameterParts[1], StandardCharsets.UTF_8);
                parameters.put(name, value);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(e);
            }
        }
        return parameters;
    }

    private PreparedSession prepareSession(final Map<String, String> requestHeaders) {
        final HttpCookie cookies = new HttpCookie(requestHeaders.get("cookie"));
        final String requestedSessionId = cookies.get("JSESSIONID");
        final SessionManager sessionManager = SessionManager.getInstance();

        if (requestedSessionId != null) {
            final Session existingSession = sessionManager.findSession(requestedSessionId);
            if (existingSession != null) {
                return new PreparedSession(existingSession, null);
            }
        }

        final String newSessionId = UUID.randomUUID().toString();
        final Session newSession = new Session(newSessionId);
        sessionManager.add(newSession);
        return new PreparedSession(newSession, newSessionId);
    }

    private boolean isLoginRequest(final String method, final String requestPath) {
        return "/login".equals(requestPath)
                && ("GET".equals(method) || "POST".equals(method));
    }

    private void handleLogin(final String method, final String requestBody,
                             final PreparedSession preparedSession,
                             final OutputStream outputStream) throws IOException {
        if ("GET".equals(method)) {
            handleLoginPage(preparedSession, outputStream);
            return;
        }

        handleLoginSubmission(requestBody, preparedSession, outputStream);
    }

    private void handleLoginPage(final PreparedSession preparedSession,
                                 final OutputStream outputStream) throws IOException {
        final User loginUser = (User) preparedSession.session().getAttribute("user");
        if (loginUser != null) {
            writeRedirectResponse(outputStream, "/index.html", preparedSession.newSessionId());
            return;
        }

        writeResourceResponse(outputStream, "/login", preparedSession.newSessionId());
    }

    private void handleLoginSubmission(final String requestBody,
                                       final PreparedSession preparedSession,
                                       final OutputStream outputStream) throws IOException {
        final Map<String, String> parameters = parseParameters(requestBody);
        final Optional<User> loginUser = authenticate(parameters);
        if (loginUser.isEmpty()) {
            writeRedirectResponse(outputStream, "/401.html", preparedSession.newSessionId());
            return;
        }

        final User user = loginUser.get();
        preparedSession.session().setAttribute("user", user);
        log.info("user : {}", user);
        writeRedirectResponse(outputStream, "/index.html", preparedSession.newSessionId());
    }

    private Optional<User> authenticate(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private boolean isRegisterRequest(final String method, final String requestPath) {
        return "POST".equals(method) && "/register".equals(requestPath);
    }

    private void handleRegister(final String requestBody,
                                final PreparedSession preparedSession,
                                final OutputStream outputStream) throws IOException {
        final Map<String, String> parameters = parseParameters(requestBody);
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        final String email = parameters.get("email");
        if (account == null || password == null || email == null) {
            throw new BadRequestException();
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        writeRedirectResponse(outputStream, "/index.html", preparedSession.newSessionId());
    }

    private void writeResourceResponse(final OutputStream outputStream,
                                       final String requestPath,
                                       final String sessionIdToSet) throws IOException {
        if ("/".equals(requestPath)) {
            final byte[] bodyBytes = "Hello world!".getBytes(StandardCharsets.UTF_8);
            writeResponse(outputStream, HTTP_OK, "text/html;charset=utf-8", bodyBytes, sessionIdToSet);
            return;
        }

        final String resourcePath = resolveResourcePath(requestPath);
        try (InputStream resourceInputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (resourceInputStream == null) {
                final byte[] bodyBytes = "Not Found".getBytes(StandardCharsets.UTF_8);
                writeResponse(outputStream, HTTP_NOT_FOUND, "text/plain;charset=utf-8", bodyBytes, sessionIdToSet);
                return;
            }

            final byte[] bodyBytes = resourceInputStream.readAllBytes();
            writeResponse(outputStream, HTTP_OK, determineContentType(resourcePath), bodyBytes, sessionIdToSet);
        }
    }

    private String resolveResourcePath(final String requestPath) {
        return switch (requestPath) {
            case "/login" -> "static/login.html";
            case "/register" -> "static/register.html";
            default -> "static" + requestPath;
        };
    }

    private String determineContentType(final String resourcePath) {
        if (resourcePath.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        if (resourcePath.endsWith(".js")) {
            return "text/javascript";
        }
        if (resourcePath.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (resourcePath.endsWith(".ico")) {
            return "image/x-icon";
        }
        return "application/octet-stream";
    }

    private void writeRedirectResponse(final OutputStream outputStream,
                                       final String location,
                                       final String sessionIdToSet) throws IOException {
        final StringBuilder responseHeaders = new StringBuilder()
                .append(HTTP_FOUND).append("\r\n");
        appendSessionCookie(responseHeaders, sessionIdToSet);
        responseHeaders.append("Location: ").append(location).append(" \r\n")
                .append("Content-Length: 0 \r\n")
                .append("\r\n");

        outputStream.write(responseHeaders.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private void writeBadRequestResponse(final OutputStream outputStream) throws IOException {
        final byte[] bodyBytes = "Bad Request".getBytes(StandardCharsets.UTF_8);
        writeResponse(outputStream, HTTP_BAD_REQUEST,
                "text/plain;charset=utf-8", bodyBytes, null);
    }

    private void writeResponse(final OutputStream outputStream, final String statusLine,
                               final String contentType, final byte[] bodyBytes,
                               final String sessionIdToSet) throws IOException {
        final StringBuilder responseHeaders = new StringBuilder()
                .append(statusLine).append("\r\n");
        appendSessionCookie(responseHeaders, sessionIdToSet);
        responseHeaders.append("Content-Type: ").append(contentType).append(" \r\n")
                .append("Content-Length: ").append(bodyBytes.length).append(" \r\n")
                .append("\r\n");

        outputStream.write(responseHeaders.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(bodyBytes);
        outputStream.flush();
    }

    private void appendSessionCookie(final StringBuilder responseHeaders, final String sessionIdToSet) {
        if (sessionIdToSet == null) {
            return;
        }

        responseHeaders.append("Set-Cookie: JSESSIONID=")
                .append(sessionIdToSet)
                .append("\r\n");
    }

    private record PreparedSession(Session session, String newSessionId) {
    }

    private static class BadRequestException extends RuntimeException {

        private BadRequestException() {
        }

        private BadRequestException(final Throwable cause) {
            super(cause);
        }
    }
}
