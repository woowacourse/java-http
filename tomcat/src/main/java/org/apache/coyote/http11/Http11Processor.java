package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_PATH = "/login";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";

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
             final var outputStream = connection.getOutputStream()
        ) {
            final RequestLine requestLine = readRequestLine(inputStream);
            final String requestPath = extractRequestPath(requestLine.requestTarget());
            final String requestQuery = extractQuery(requestLine.requestTarget());
            final Map<String, String> requestQueryParameters = parseQuery(requestQuery);
            logUserIfAuthenticated(requestPath, requestQueryParameters);
            final String contentType = resolveContentType(requestPath);
            final byte[] responseBody = readResponseBody(requestPath);
            writeResponse(outputStream, contentType, responseBody);
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestLine readRequestLine(InputStream inputStream) throws IOException {
        final BufferedReader reader = getReader(inputStream);
        String line = reader.readLine();

        if (line == null) {
            throw new IllegalArgumentException("HTTP request line must not be null");
        }

        return RequestLine.from(line);
    }

    private BufferedReader getReader(InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }

    private byte[] readResponseBody(final String requestPath) throws IOException {
        if (requestPath.equals("/")) {
            return defaultResponseBody();
        }
        try (InputStream resourceStream = findResource(requestPath)) {
            return readResource(resourceStream);
        }
    }

    private InputStream findResource(final String requestPath) {
        return getClass()
                .getClassLoader()
                .getResourceAsStream(resolveResourcePath(requestPath));
    }

    private byte[] readResource(final InputStream resourceStream) throws IOException {
        if (resourceStream == null) {
            return defaultResponseBody();
        }
        return resourceStream.readAllBytes();
    }

    private byte[] defaultResponseBody() {
        return DEFAULT_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8);
    }

    private void writeResponse(final OutputStream outputStream, final String contentType, final byte[] responseBody)
            throws IOException {
        final String responseHead = createResponseHead(contentType, responseBody.length);
        outputStream.write(responseHead.getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private String createResponseHead(final String contentType, final int contentLength) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + contentLength + " ",
                "",
                "");
    }

    private String resolveContentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return CSS_CONTENT_TYPE;
        }
        return HTML_CONTENT_TYPE;
    }

    private String extractRequestPath(final String requestTarget) {
        final int queryIndex = requestTarget.indexOf("?");
        if (queryIndex < 0) {
            return requestTarget;
        }

        return requestTarget.substring(0, queryIndex);
    }

    private String extractQuery(final String requestTarget) {
        final int queryIndex = requestTarget.indexOf("?");
        if (queryIndex < 0 || queryIndex == requestTarget.length() - 1) {
            return "";
        }

        return requestTarget.substring(queryIndex + 1);
    }

    private String resolveResourcePath(final String requestPath) {
        if (requestPath.equals(LOGIN_PATH)) {
            return "static/login.html";
        }
        return "static" + requestPath;
    }

    private void logUserIfAuthenticated(final String requestPath, final Map<String, String> queryParameters) {
        if (!requestPath.equals(LOGIN_PATH) || !hasCredentials(queryParameters)) {
            return;
        }
        final String account = queryParameters.get("account");
        final String password = queryParameters.get("password");
        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("User: {}", user));
    }

    private boolean hasCredentials(final Map<String, String> queryParameters) {
        return queryParameters.containsKey("account") && queryParameters.containsKey("password");
    }

    private Map<String, String> parseQuery(final String query) {
        if (query.isBlank()) {
            return Map.of();
        }
        final Map<String, String> queryMap = new HashMap<>();
        for (final String param : query.split("&")) {
            final String[] keyAndValue = param.split("=", 2);
            validateQueryParameter(keyAndValue);
            queryMap.put(keyAndValue[0], keyAndValue[1]);
        }
        return Map.copyOf(queryMap);
    }

    private void validateQueryParameter(final String[] keyAndValue) {
        if (keyAndValue.length != 2) {
            throw new IllegalArgumentException("Invalid query parameter");
        }
    }
}
