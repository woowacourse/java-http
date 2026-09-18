package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_ROOT = "static";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String JAVASCRIPT_CONTENT_TYPE = "application/javascript";
    private static final byte[] DEFAULT_BODY = "Hello world!".getBytes(StandardCharsets.UTF_8);

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

            readHeaders(reader);

            final var requestUri = extractRequestUri(requestLine);
            final var requestPath = extractRequestPath(requestUri);
            final var queryParameters = parseQueryParameters(requestUri);

            logLoginUser(requestPath, queryParameters);

            final var responseBody = readResponseBody(requestPath);
            final var contentType = findContentType(requestPath);

            writeResponse(outputStream, contentType, responseBody);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void readHeaders(final BufferedReader reader) throws IOException {
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                return;
            }
        }
    }

    private String extractRequestUri(final String requestLine) {
        final var requestParts = requestLine.trim().split("\\s+");

        if (requestParts.length != 3) {
            throw new UncheckedServletException(
                    new IllegalArgumentException("Invalid request line: " + requestLine)
            );
        }

        return requestParts[1];
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

        final Map<String, String> parameters = new HashMap<>();
        final var queryString = requestUri.substring(queryIndex + 1);

        for (final var parameter : queryString.split("&")) {
            final var nameAndValue = parameter.split("=", 2);
            if (nameAndValue.length == 2) {
                parameters.put(
                        decode(nameAndValue[0]),
                        decode(nameAndValue[1])
                );
            }
        }

        return Map.copyOf(parameters);
    }

    private String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private void logLoginUser(
            final String requestPath,
            final Map<String, String> queryParameters
    ) {
        if (!"/login".equals(requestPath)) {
            return;
        }

        final var account = queryParameters.get("account");
        final var password = queryParameters.get("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("회원 조회 결과: account={}", user.getAccount()));
    }

    private byte[] readResponseBody(final String requestPath) throws IOException {
        if ("/".equals(requestPath)) {
            return DEFAULT_BODY.clone();
        }

        validateRequestUri(requestPath);

        final var resourcePath = findResourcePath(requestPath);
        final var classLoader = Http11Processor.class.getClassLoader();

        try (final var resource = classLoader.getResourceAsStream(resourcePath)) {
            if (resource == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            return resource.readAllBytes();
        }
    }

    private String findResourcePath(final String requestPath) {
        if ("/login".equals(requestPath)) {
            return STATIC_ROOT + "/login.html";
        }

        return STATIC_ROOT + requestPath;
    }

    private void validateRequestUri(final String requestUri) {
        final var invalidPath = !requestUri.startsWith("/")
                || requestUri.contains("..")
                || requestUri.contains("\\");

        if (invalidPath) {
            throw new UncheckedServletException(
                    new IllegalArgumentException("Invalid request URI: " + requestUri)
            );
        }
    }

    private String findContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return CSS_CONTENT_TYPE;
        }

        if (requestUri.endsWith(".js")) {
            return JAVASCRIPT_CONTENT_TYPE;
        }

        return HTML_CONTENT_TYPE;
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String contentType,
            final byte[] responseBody
    ) throws IOException {
        final var responseHeaders = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.length + " ",
                "",
                ""
        );

        outputStream.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }
}
