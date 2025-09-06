package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final var httpRequest = parseRequest(inputStream);
            final var response = dispatch(httpRequest);
            outputStream.write(response.getResponseBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Request parseRequest(final InputStream inputStream) throws IOException {
        final var reader = new BufferedReader(new InputStreamReader(inputStream));
        final String requestLineString = reader.readLine();
        if (requestLineString == null || requestLineString.isBlank()) {
            return Http11Request.createInvalid();
        }
        final var requestLine = parseRequestLine(requestLineString);
        final var headers = parseHeaders(reader);
        final String body = parseBody(reader, headers);
        return new Http11Request(requestLine.method(), requestLine.path(), requestLine.queryParams(), headers, body);
    }

    private RequestLine parseRequestLine(final String line) {
        final var parts = line.split(" ");
        final String method = parts[0];
        final String requestTarget = parts[1];
        String path = requestTarget;
        String queryString = "";
        if (requestTarget.contains("?")) {
            final int queryIndex = requestTarget.indexOf("?");
            path = requestTarget.substring(0, queryIndex);
            queryString = requestTarget.substring(queryIndex + 1);
        }
        final var queryParams = parseQueryString(queryString);
        return new RequestLine(method, path, queryParams);
    }

    private Map<String, String> parseHeaders(final BufferedReader reader) throws IOException {
        final var headers = new HashMap<String, String>();
        String headerLine;
        while (!(headerLine = reader.readLine()).isBlank()) {
            final var header = headerLine.split(": ");
            headers.put(header[0].trim(), header[1].trim());
        }
        return headers;
    }

    private String parseBody(
            final BufferedReader reader,
            final Map<String, String> headers
    ) throws IOException {
        if (!headers.containsKey("Content-Length")) {
            return "";
        }
        final int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private Map<String, String> parseQueryString(final String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return Collections.emptyMap();
        }
        final Map<String, String> params = new HashMap<>();
        final String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    private Http11Response dispatch(final Http11Request httpRequest) {
        final var path = httpRequest.getPath();
        if ("/".equals(path)) {
            return new Http11Response(200, "text/html;charset=utf-8", "Hello world!");
        }
        if ("/login".equals(path)) {
            handleLogin(httpRequest);
            return serveStaticFile("/login.html");
        }
        return serveStaticFile(path);
    }

    private void handleLogin(final Http11Request httpRequest) {
        final var queryParams = httpRequest.getQueryParams();
        if (queryParams.containsKey("account") && queryParams.containsKey("password")) {
            final String account = queryParams.get("account");
            final String password = queryParams.get("password");
            InMemoryUserRepository.findByAccount(account)
                    .filter(user -> user.checkPassword(password))
                    .ifPresent(user -> log.info("login success: {}", user));
        }
    }

    private Http11Response serveStaticFile(final String path) {
        return readStaticResource(path)
                .map(body -> new Http11Response(200, getContentType(path), body))
                .orElseGet(this::serveNotFoundPage);
    }

    private Http11Response serveNotFoundPage() {
        return readStaticResource("/404.html")
                .map(body -> new Http11Response(404, "text/html;charset=utf-8", body))
                .orElse(new Http11Response(404, "text/html;charset=utf-8", "404 Not Found"));
    }

    private Optional<byte[]> readStaticResource(final String path) {
        final String resourcePath = "static" + path;
        try (final InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                return Optional.empty();
            }
            return Optional.of(resourceStream.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
