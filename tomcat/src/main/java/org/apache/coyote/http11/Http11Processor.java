package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
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
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String NOT_FOUND_RESOURCE_PATH = "static/404.html";

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final var requestUri = extractRequestUri(reader.readLine());

            final var request = toRequest(requestUri);
            handleRequest(request, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String extractRequestUri(final String requestLine) {
        log.info("request: {}", requestLine);
        return requestLine.split(" ")[1];
    }

    private static Request toRequest(final String requestUri) {
        final int index = requestUri.indexOf("?");
        if (index == -1) {
            return new Request(requestUri, Map.of());
        }

        final String queryString = requestUri.substring(index + 1);
        if (queryString.isEmpty()) {
            return new Request(requestUri, Map.of());
        }
        return new Request(requestUri.substring(0, index), Map.copyOf(parseQueries(queryString)));
    }

    private static Map<String, String> parseQueries(final String queryString) {
        final String[] queries = queryString.split("&");
        final Map<String, String> params = new HashMap<>();
        for (final String query : queries) {
            final String[] pair = query.split("=", 2);
            if (pair.length == 2) {
                final String name = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                params.put(name, value);
            }
        }
        return params;
    }

    private void handleRequest(final Request request, final OutputStream outputStream) throws IOException {
        if ("/login".equals(request.path()) && !request.params().isEmpty()) {
            if (isValidAuth(request.params())) {
                writeResponse(outputStream, "302 Found", Map.of("Location", "/index.html"), new byte[0]);
                return;
            }
            final String invalidRedirectUri = "/login.html?error=invalid_credentials";
            writeResponse(outputStream, "302 Found", Map.of("Location", invalidRedirectUri), new byte[0]);
        } else if ("/".equals(request.path())) {
            final var body = "Hello world!".getBytes(StandardCharsets.UTF_8);
            writeResponse(outputStream, "200 OK", contentTypeHeader("text/html"), body);
        } else {
            writeResource(outputStream, "200 OK", STATIC_RESOURCE_PATH + appendHtmlExtension(request.path()));
        }
    }

    private boolean isValidAuth(final Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        if (account == null || password == null) {
            return false;
        }

        return InMemoryUserRepository.findByAccount(account)
                        .filter(user -> user.checkPassword(password))
                        .isPresent();
    }

    private void writeResource(
            final OutputStream outputStream,
            final String status,
            final String resourcePath
    ) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                writeResource(outputStream, "404 Not Found", NOT_FOUND_RESOURCE_PATH);
                return;
            }

            final var contentType = MimeTypeResolver.resolve(resourcePath);
            writeResponse(outputStream, status, contentTypeHeader(contentType), resource.readAllBytes());
        }
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String status,
            final Map<String, String> headers,
            final byte[] responseBody
    ) throws IOException {
        final var responseHeader = new StringBuilder().append("HTTP/1.1 %s \r\n".formatted(status));
        headers.forEach((name, value) -> responseHeader.append("%s: %s \r\n".formatted(name, value)));
        responseHeader.append("Content-Length: %d \r\n".formatted(responseBody.length)).append("\r\n");

        outputStream.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private Map<String, String> contentTypeHeader(final String contentType) {
        if (contentType.startsWith("text/")) {
            return Map.of("Content-Type", contentType + ";charset=utf-8");
        }
        return Map.of("Content-Type", contentType);
    }

    private static String appendHtmlExtension(final String resourcePath) {
        final var fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        if (fileName.contains(".")) {
            return resourcePath;
        }
        return resourcePath + ".html";
    }
}
