package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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
            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));

            final var requestUri = parseRequestUri(reader);
            readHeaders(reader);
            final var requestPath = extractPath(requestUri);
            final var contentType = determineContentType(requestPath);

            final byte[] responseBody;

            if (requestPath.equals("/")) {
                responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            } else if (requestPath.equals("/login")) {
                final var parameters = parseQueryString(requestUri);
                final var authenticated = authenticate(parameters);

                if (authenticated) {
                    responseBody = readStaticResource("/index.html");
                } else {
                    responseBody = readStaticResource("/401.html");
                }
            } else {
                responseBody = readStaticResource(requestPath);
            }

            writeResponse(outputStream, responseBody, contentType);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestUri(final BufferedReader reader) throws IOException {
        final var line = reader.readLine();

        if (line == null || line.isEmpty()) {
            throw new IllegalArgumentException("HTTP 요청 라인이 비어있습니다.");
        }

        final var tokens = line.trim().split("\\s+");

        if (tokens.length != 3) {
            throw new IllegalArgumentException("올바르지 않은 HTTP 요청 라인입니다.");
        }

        return tokens[1];
    }

    private void readHeaders(final BufferedReader reader) throws IOException {
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            log.debug("HTTP header: {}", line);
        }
    }

    private byte[] readStaticResource(final String path) throws IOException {
        final var resourcePath = "static" + path;

        final var resource = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (resource == null) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다." + resourcePath);
        }

        try (resource) {
            return resource.readAllBytes();
        }
    }

    private void writeResponse(final OutputStream outputStream, final byte[] bytes, String contentType) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + bytes.length + " ",
                "\r\n");
        outputStream.write(response.getBytes());
        outputStream.write(bytes);
        outputStream.flush();
    }

    private String determineContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private String extractPath(final String uri) {
        String[] parsedUri = uri.split("\\?");
        return parsedUri[0];
    }

    private Map<String, String> parseQueryString(final String uri) {
        final var queryIndex = uri.indexOf('?');

        if (queryIndex < 0 || queryIndex == uri.length() - 1) {
            return Map.of();
        }

        final var queryString = uri.substring(queryIndex + 1);

        return Arrays.stream(queryString.split("&"))
                .map(parameter -> parameter.split("=", 2))
                .collect(Collectors.toMap(
                        pair -> pair[0],
                        pair -> pair[1]
                ));
    }

    private boolean authenticate(Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");

        if (account == null || password == null) {
            return false;
        }

        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }
}
