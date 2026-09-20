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
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int REQUEST_LINE_PART_COUNT = 3;
    private static final int REQUEST_TARGET_INDEX = 1;
    private static final int QUERY_PARAMETER_PART_COUNT = 2;
    private static final String LOGIN_PATH = "/login";
    private static final String CRLF = "\r\n";

    private final Socket connection;
    private final ResponseContentResolver responseContentResolver;

    public Http11Processor(final Socket connection) {
        this(connection, new ResponseContentResolver());
    }

    Http11Processor(final Socket connection, final ResponseContentResolver responseContentResolver) {
        this.connection = connection;
        this.responseContentResolver = Objects.requireNonNull(responseContentResolver);
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
            final URI requestUri;
            try {
                requestUri = readRequestUri(reader);
            } catch (IOException e) {
                log.warn("Failed to read HTTP request", e);
                return;
            }
            if (requestUri == null) {
                return;
            }

            final String path = requestUri.getPath();
            if (path == null) {
                return;
            }
            final String queryString = requestUri.getRawQuery();
            if (LOGIN_PATH.equals(path) && queryString != null) {
                logMatchingLoginUser(queryString);
            }

            final var response = resolveResponse(path);

            try {
                writeResponse(outputStream, response);
            } catch (IOException e) {
                log.warn("Failed to write HTTP response", e);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error("Failed to handle HTTP connection", e);
        }
    }

    private URI readRequestUri(final BufferedReader reader) throws IOException {
        final var requestLine = reader.readLine();
        if (requestLine == null) {
            return null;
        }

        final var requestParts = requestLine.split(" ");
        if (requestParts.length != REQUEST_LINE_PART_COUNT) {
            return null;
        }
        if (!skipHeaders(reader)) {
            return null;
        }
        try {
            return URI.create(requestParts[REQUEST_TARGET_INDEX]);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid HTTP request target");
            return null;
        }
    }

    private boolean skipHeaders(final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void logMatchingLoginUser(final String queryString) {
        final Map<String, String> parameters = parseQueryParameters(queryString);
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("login user found: {}", user.getAccount()));
    }

    private Map<String, String> parseQueryParameters(final String queryString) {
        final var nameValuePairs = Arrays.stream(queryString.split("&"))
                .map(parameter -> parameter.split("=", QUERY_PARAMETER_PART_COUNT))
                .toList();
        if (nameValuePairs.stream().anyMatch(pair -> pair.length != QUERY_PARAMETER_PART_COUNT)) {
            return Map.of();
        }

        return nameValuePairs.stream().collect(Collectors.toMap(
                pair -> pair[0],
                pair -> pair[1],
                (previous, replacement) -> replacement));
    }

    private HttpResponse resolveResponse(final String path) {
        try {
            return HttpResponse.ok(responseContentResolver.resolve(path));
        } catch (HttpException e) {
            log.error(e.getMessage(), e);
            return HttpResponse.error(e.status());
        }
    }

    private void writeResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        final var content = response.content();
        final var headers = String.join(CRLF,
                "HTTP/1.1 " + response.status().code() + " " + response.status().reasonPhrase() + " ",
                "Content-Type: " + content.contentType() + " ",
                "Content-Length: " + content.body().length + " ",
                "",
                "");

        outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
        outputStream.write(content.body());
        outputStream.flush();
    }
}
