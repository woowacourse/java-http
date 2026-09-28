package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );

            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final var requestParts = splitRequestLine(requestLine);
            final var requestMethod = requestParts[0];
            final var requestUri = requestParts[1];
            final var requestHeaders = readHeaders(reader);
            final var requestPath = extractRequestPath(requestUri);
            final var requestBody = readRequestBody(reader, requestHeaders);
            final var parameters = findParameters(requestMethod, requestUri, requestBody);
            final var requestedSessionId = HttpCookie.parse(requestHeaders.get("Cookie")).get("JSESSIONID");
            final var session = findSession(requestedSessionId);
            final var sessionCookie = createSessionCookie(requestedSessionId, session);

            if (isLoginPageRequest(requestMethod, requestPath, session)) {
                writeRedirect(outputStream, "/index.html", sessionCookie);
                return;
            }

            final var redirectLocation = processForm(requestMethod, requestPath, parameters, session);
            if (redirectLocation != null) {
                writeRedirect(outputStream, redirectLocation, sessionCookie);
                return;
            }

            final var responseBody = readResponseBody(requestPath);
            final var contentType = findContentType(requestPath);

            writeResponse(outputStream, contentType, responseBody, sessionCookie);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }

            final var separatorIndex = line.indexOf(':');
            if (separatorIndex > 0) {
                headers.put(
                        line.substring(0, separatorIndex).trim(),
                        line.substring(separatorIndex + 1).trim()
                );
            }
        }

        return headers;
    }

    private String[] splitRequestLine(final String requestLine) {
        final var requestParts = requestLine.trim().split("\\s+");

        if (requestParts.length != 3) {
            throw new UncheckedServletException(
                    new IllegalArgumentException("유효하지 않은 요청 라인입니다: " + requestLine)
            );
        }

        return requestParts;
    }

    private String readRequestBody(
            final BufferedReader reader,
            final Map<String, String> requestHeaders
    ) throws IOException {
        final var contentLength = requestHeaders.get("Content-Length");
        if (contentLength == null) {
            return "";
        }

        final var buffer = new char[Integer.parseInt(contentLength)];
        var offset = 0;

        while (offset < buffer.length) {
            final var readLength = reader.read(buffer, offset, buffer.length - offset);
            if (readLength < 0) {
                break;
            }
            offset += readLength;
        }

        return new String(buffer, 0, offset);
    }

    private String extractRequestPath(final String requestUri) {
        final var queryIndex = requestUri.indexOf('?');

        if (queryIndex < 0) {
            return requestUri;
        }

        return requestUri.substring(0, queryIndex);
    }

    private Map<String, String> parseQueryParameters(final String requestUri) {
        final var queryIndex = requestUri.indexOf('?');

        if (queryIndex < 0 || queryIndex == requestUri.length() - 1) {
            return Map.of();
        }

        final var queryString = requestUri.substring(queryIndex + 1);
        return parseParameters(queryString);
    }

    private Map<String, String> findParameters(
            final String requestMethod,
            final String requestUri,
            final String requestBody
    ) {
        if ("POST".equals(requestMethod)) {
            return parseParameters(requestBody);
        }

        return parseQueryParameters(requestUri);
    }

    private Map<String, String> parseParameters(final String value) {
        if (value.isBlank()) {
            return Map.of();
        }

        final Map<String, String> parameters = new HashMap<>();

        for (final var parameter : value.split("&")) {
            final var nameAndValue = parameter.split("=", 2);
            if (nameAndValue.length == 2) {
                parameters.put(
                        URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(nameAndValue[1], StandardCharsets.UTF_8)
                );
            }
        }

        return parameters;
    }

    private String processForm(
            final String requestMethod,
            final String requestPath,
            final Map<String, String> parameters,
            final Session session
    ) {
        if (!"POST".equals(requestMethod)) {
            return null;
        }

        final var account = parameters.get("account");
        final var password = parameters.get("password");

        if ("/register".equals(requestPath)) {
            final var email = parameters.get("email");
            InMemoryUserRepository.save(new User(account, password, email));
            return "/index.html";
        }

        if ("/login".equals(requestPath)) {
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

    private Session findSession(final String sessionId) {
        final var sessionManager = SessionManager.getInstance();

        if (sessionId != null) {
            final var session = sessionManager.findSession(sessionId);
            if (session instanceof Session) {
                return (Session) session;
            }
        }

        return sessionManager.createSession();
    }

    private String createSessionCookie(final String requestedSessionId, final Session session) {
        if (session.getId().equals(requestedSessionId)) {
            return null;
        }

        return "JSESSIONID=" + session.getId();
    }

    private boolean isLoginPageRequest(
            final String requestMethod,
            final String requestPath,
            final Session session
    ) {
        return "GET".equals(requestMethod)
                && "/login".equals(requestPath)
                && session.getAttribute(USER_SESSION_KEY) != null;
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String contentType,
            final byte[] responseBody,
            final String sessionCookie
    ) throws IOException {
        final var responseHeaders = "HTTP/1.1 200 OK \r\n"
                + createSetCookieHeader(sessionCookie)
                + String.join("\r\n",
                        "Content-Type: " + contentType + " ",
                        "Content-Length: " + responseBody.length + " ",
                        "",
                        ""
                );

        outputStream.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private void writeRedirect(
            final OutputStream outputStream,
            final String location,
            final String sessionCookie
    ) throws IOException {
        final var response = "HTTP/1.1 302 Found\r\n"
                + createSetCookieHeader(sessionCookie)
                + String.join("\r\n",
                        "Location: " + location,
                        "Content-Length: 0",
                        "",
                        ""
                );

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String createSetCookieHeader(final String sessionCookie) {
        if (sessionCookie == null) {
            return "";
        }

        return "Set-Cookie: " + sessionCookie + "\r\n";
    }
}
