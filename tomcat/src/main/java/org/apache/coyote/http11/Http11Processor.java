package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final RequestLine requestLine = readRequestLine(inputStream);
            final String requestTarget = requestLine.requestTarget();
            final String requestPath = extractRequestPath(requestTarget);
            final String requestQuery = extractQuery(requestTarget);
            final Map<String, String> queryParameters = parseQuery(requestQuery);
            final String resourcePath = resolveResourcePath(requestPath);

            logUserIfAuthenticated(requestPath, queryParameters);

            var responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);

            try (InputStream resourceStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream(resourcePath)) {

                if (resourceStream != null && !requestPath.equals("/")) {
                    responseBody = resourceStream.readAllBytes();
                }
            }

            String responseContentType = "text/html;charset=utf-8";
            if (requestPath.endsWith(".css")) {
                responseContentType = "text/css;charset=utf-8";
            }

            final String responseHead = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + responseContentType + " ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    "");

            outputStream.write(responseHead.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestLine readRequestLine(InputStream inputStream) throws IOException {
        final BufferedReader reader = getReader(inputStream);
        String line = reader.readLine();

        if (line == null) {
            throw new IllegalStateException("request line is null");
        }

        return RequestLine.from(line);
    }

    private BufferedReader getReader(InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
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
        if (requestPath.equals("/login")) {
            return "static/login.html";
        }

        return "static" + requestPath;
    }

    private void logUserIfAuthenticated(
            final String requestPath,
            final Map<String, String> queryParameters
    ) {
        if (!requestPath.equals("/login")) {
            return;
        }

        final String account = queryParameters.get("account");
        final String password = queryParameters.get("password");
        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("User: {}", user));
    }

    private Map<String, String> parseQuery(final String query) {
        if (query.isBlank()) {
            return Map.of();
        }

        final Map<String, String> queryMap = new HashMap<>();

        for (final String param : query.split("&")) {
            final String[] keyAndValue = param.split("=", 2);
            if (keyAndValue.length != 2) {
                throw new IllegalArgumentException("Invalid query parameter: " + param);
            }
            queryMap.put(keyAndValue[0], keyAndValue[1]);
        }

        return Map.copyOf(queryMap);
    }
}
