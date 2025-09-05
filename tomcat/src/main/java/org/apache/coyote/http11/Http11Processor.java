package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.LoginService;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final LoginService loginService = new LoginService();

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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            final var uri = parseRequestUri(requestLine);
            final var pathAndQuery = extractPathAndQuery(uri);
            final var path = pathAndQuery.get("path");
            final var params = parseQueryString(pathAndQuery.get("query"));

            if ("/login".equals(path)) {
                final var account = params.get("account");
                final var password = params.get("password");

                if (account == null || password == null) {
                    final var resourceUrl = Objects.requireNonNull(getClass().getClassLoader()
                            .getResource("static/login.html"));
                    final var body = readResourceFile(resourceUrl);

                    writeResponse(
                            outputStream,
                            200,
                            "OK",
                            null,
                            body,
                            "text/html;charset=utf-8"
                    );
                    return;
                }

                if (loginService.login(account, password)) {
                    writeResponse(
                            outputStream,
                            302,
                            "Found",
                            Map.of("Location", "/index.html"),
                            null,
                            "text/html;charset=utf-8"
                    );
                } else {
                    writeResponse(
                            outputStream,
                            302,
                            "Found",
                            Map.of("Location", "/401.html"),
                            null,
                            "text/html;charset=utf-8"
                    );
                }
                return;
            }

            final var resourcePath = "static" + path;
            final var resourceUrl = getClass().getClassLoader()
                    .getResource(resourcePath);

            if (resourceUrl != null && !Files.isDirectory(Path.of(resourceUrl.toURI()))) {
                final var body = readResourceFile(resourceUrl);
                final var contentType = detectContentType(resourceUrl);

                writeResponse(outputStream, 200, "OK", null, body, contentType);
                return;
            } else {
                final var body = "Hello world!".getBytes(StandardCharsets.UTF_8);

                writeResponse(outputStream, 200, "OK", null, body, "text/html;charset=utf-8");
                return;
            }
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            try {
                final var resourceUrl = getClass().getClassLoader()
                        .getResource("static/500.html");
                final byte[] body = (resourceUrl != null)
                        ? readResourceFile(resourceUrl)
                        : "Internal Server Error".getBytes(StandardCharsets.UTF_8);

                writeResponse(
                        connection.getOutputStream(),
                        500,
                        "Internal Server Error",
                        null,
                        body,
                        "text/html;charset=utf-8"
                );
            } catch (IOException | URISyntaxException ex) {
                log.error("Failed to send 500 response: {}", ex.getMessage(), ex);
            }
        }
    }

    private String parseRequestUri(final String requestLine) {
        final var parts = requestLine.split(" ");

        return parts[1];
    }

    private Map<String, String> extractPathAndQuery(final String uri) {
        final var result = new HashMap<String, String>();
        final var index = uri.indexOf("?");

        if (index != -1) {
            result.put("path", uri.substring(0, index));
            result.put("query", uri.substring(index + 1));
        } else {
            result.put("path", uri);
            result.put("query", null);
        }

        return result;
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final var paramMap = new HashMap<String, String>();

        if (queryString == null) {
            return paramMap;
        }

        final var queries = queryString.split("&");
        for (final var query : queries) {
            final var keyValue = query.split("=", 2);
            if (keyValue.length == 2) {
                paramMap.put(keyValue[0], keyValue[1]);
            }
        }

        return paramMap;
    }

    private byte[] readResourceFile(final URL resourceUrl) throws IOException, URISyntaxException {
        final var path = Path.of(resourceUrl.toURI());

        return Files.readAllBytes(path);
    }

    private String detectContentType(final URL resourceUrl) throws IOException, URISyntaxException {
        final var path = Path.of(resourceUrl.toURI());
        final var contentType = Files.probeContentType(path);

        return contentType != null ? contentType : "text/plain;charset=utf-8";
    }

    private void writeResponse(
            final OutputStream outputStream,
            final int statusCode,
            final String statusMessage,
            final Map<String, String> extraHeaders,
            final byte[] body,
            final String contentType
    ) throws IOException { // TODO. IOException을 RuntimeException 전환 고려
        final var headers = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(statusMessage)
                .append("\r\n")
                .append("Content-Type: ")
                .append(contentType)
                .append("\r\n");

        if (extraHeaders != null) {
            extraHeaders.forEach((k, v) -> headers.append(k)
                    .append(": ")
                    .append(v)
                    .append("\r\n"));
        }

        headers.append("\r\n");
        outputStream.write(headers.toString()
                .getBytes(StandardCharsets.UTF_8));

        if (body != null) {
            outputStream.write(body);
        }

        outputStream.flush();
    }
}
