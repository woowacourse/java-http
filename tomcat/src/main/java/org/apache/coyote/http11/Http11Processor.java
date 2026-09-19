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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int REQUEST_LINE_PART_COUNT = 3;
    private static final int REQUEST_TARGET_INDEX = 1;
    private static final String INDEX_PATH = "/index.html";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_PAGE_PATH = "/login.html";
    private static final String CSS_PATH = "/css/styles.css";
    private static final Set<String> JAVASCRIPT_PATHS = Set.of(
            "/js/scripts.js",
            "/assets/chart-area.js",
            "/assets/chart-bar.js",
            "/assets/chart-pie.js");
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";
    private static final String JAVASCRIPT_CONTENT_TYPE = "text/javascript;charset=utf-8";
    private static final String CRLF = "\r\n";

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
            final var requestTarget = readRequestTarget(reader);
            if (requestTarget == null) {
                return;
            }

            final String[] targetParts = requestTarget.split("\\?", 2);
            final String path = targetParts[0];
            if (LOGIN_PATH.equals(path) && targetParts.length == 2) {
                logMatchingLoginUser(targetParts[1]);
            }

            writeResponse(outputStream, createResponse(path));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestTarget(final BufferedReader reader) throws IOException {
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
        return requestParts[REQUEST_TARGET_INDEX];
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
        final var pairs = Arrays.stream(queryString.split("&"))
                .map(parameter -> parameter.split("=", 2))
                .toList();
        if (pairs.stream().anyMatch(pair -> pair.length != 2)) {
            return Map.of();
        }

        return pairs.stream().collect(Collectors.toMap(
                pair -> pair[0],
                pair -> pair[1],
                (previous, replacement) -> replacement));
    }

    private ResponseContent createResponse(final String path) throws IOException {
        if (INDEX_PATH.equals(path)) {
            return new ResponseContent(HTML_CONTENT_TYPE, readResource(path));
        }
        if (LOGIN_PATH.equals(path)) {
            return new ResponseContent(HTML_CONTENT_TYPE, readResource(LOGIN_PAGE_PATH));
        }
        if (CSS_PATH.equals(path)) {
            return new ResponseContent(CSS_CONTENT_TYPE, readResource(path));
        }
        if (JAVASCRIPT_PATHS.contains(path)) {
            return new ResponseContent(JAVASCRIPT_CONTENT_TYPE, readResource(path));
        }
        return new ResponseContent(HTML_CONTENT_TYPE, "Hello world!".getBytes(StandardCharsets.UTF_8));
    }

    private byte[] readResource(final String requestTarget) throws IOException {
        final var resourcePath = "static" + requestTarget;
        try (final var resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                throw new IOException(resourcePath + " not found");
            }
            return resource.readAllBytes();
        }
    }

    private void writeResponse(final OutputStream outputStream, final ResponseContent response) throws IOException {
        final var headers = String.join(CRLF,
                "HTTP/1.1 200 OK ",
                "Content-Type: " + response.contentType() + " ",
                "Content-Length: " + response.body().length + " ",
                "",
                "");

        outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
        outputStream.write(response.body());
        outputStream.flush();
    }

    private record ResponseContent(String contentType, byte[] body) {
    }
}
