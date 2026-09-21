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
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String NOT_FOUND_PATH = "/404.html";
    private static final String OK_STATUS_LINE = "HTTP/1.1 200 OK";
    private static final String FOUND_STATUS_LINE = "HTTP/1.1 302 Found";
    private static final String NOT_FOUND_STATUS_LINE = "HTTP/1.1 404 Not Found";
    private static final String INDEX_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String NOT_FOUND_RESPONSE_BODY = "404 Not Found";

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
            sendResponse(requestLine, outputStream);
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestLine readRequestLine(InputStream inputStream) throws IOException {
        final BufferedReader reader = getReader(inputStream);
        String line = reader.readLine();
        return RequestLine.from(line);
    }

    private BufferedReader getReader(InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }

    private void sendResponse(final RequestLine requestLine, final OutputStream outputStream) throws IOException {
        final String requestPath = extractRequestPath(requestLine.requestTarget());

        if (ROOT_PATH.equals(requestPath)) {
            writeResponse(
                    outputStream,
                    OK_STATUS_LINE,
                    HTML_CONTENT_TYPE,
                    readDefaultResponseBody()
            );
            return;
        }

        final String queryString = extractQuery(requestLine.requestTarget());
        if (LOGIN_PATH.equals(requestPath) && !queryString.isBlank()) {
            final Map<String, String> queryParameters = parseQuery(queryString);

            if (checkAuthentication(queryParameters)) {
                sendRedirect(outputStream, INDEX_PATH);
                return;
            }
            sendRedirect(outputStream, UNAUTHORIZED_PATH);
            return;
        }

        final Optional<byte[]> resourceBody = findResourceBody(requestPath);

        if (resourceBody.isEmpty()) {
            writeResponse(
                    outputStream,
                    NOT_FOUND_STATUS_LINE,
                    HTML_CONTENT_TYPE,
                    readNotFoundResponseBody()
            );
            return;
        }

        writeResponse(
                outputStream,
                OK_STATUS_LINE,
                resolveContentType(requestPath),
                resourceBody.get()
        );
    }

    private void sendRedirect(final OutputStream outputStream, final String location) throws IOException {
        final String response = String.join("\r\n",
                FOUND_STATUS_LINE + " ",
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                "");

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String statusLine,
            final String contentType,
            final byte[] responseBody
    ) throws IOException {
        final String responseHead = createResponseHead(
                statusLine,
                contentType,
                responseBody.length
        );

        outputStream.write(responseHead.getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private Optional<byte[]> findResourceBody(final String requestPath) throws IOException {
        try (InputStream resourceStream = findResource(requestPath)) {
            if (resourceStream == null) {
                return Optional.empty();
            }
            return Optional.of(resourceStream.readAllBytes());
        }
    }

    private InputStream findResource(final String requestPath) {
        final String resourcePath = resolveResourcePath(requestPath);
        return getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath);
    }

    private byte[] readNotFoundResponseBody() throws IOException {
        return findResourceBody(NOT_FOUND_PATH)
                .orElseGet(() -> NOT_FOUND_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] readDefaultResponseBody() {
        return DEFAULT_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8);
    }

    private String createResponseHead(
            final String statusLine,
            final String contentType,
            final int contentLength
    ) {
        return String.join("\r\n",
                statusLine + " ",
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

    private boolean checkAuthentication(final Map<String, String> queryParameters) {
        if (!hasCredentials(queryParameters)) {
            return false;
        }

        final String account = queryParameters.get("account");
        final String password = queryParameters.get("password");

        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
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
