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

            final byte[] responseBody;
            final String contentType;

            if ("/login".equals(path)) {
                final var resourceUrl = Objects.requireNonNull(getClass().getClassLoader()
                        .getResource("static/login.html"));
                responseBody = readResourceFile(resourceUrl);
                contentType = "text/html;charset=utf-8 ";

                final var account = params.get("account");
                final var password = params.get("password");
                InMemoryUserRepository.findByAccount(account)
                        .ifPresent(user -> {
                            if (user.checkPassword(password)) {
                                log.info("user: {}", user);
                            }
                        });

            } else {
                final var resourcePath = "static" + path;
                final var resourceUrl = getClass().getClassLoader()
                        .getResource(resourcePath);

                if (resourceUrl != null && !Files.isDirectory(Path.of(resourceUrl.toURI()))) {
                    responseBody = readResourceFile(resourceUrl);
                    contentType = detectContentType(resourceUrl);
                } else {
                    responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
                    contentType = "text/html;charset=utf-8 ";
                }
            }

            writeResponse(outputStream, responseBody, contentType);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
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

        return contentType != null ? contentType : "text/plain;charset=utf-8 ";
    }

    private void writeResponse(
            final OutputStream outputStream,
            final byte[] body,
            final String contentType
    ) throws IOException {
        final var headers = String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType,
                "Content-Length: " + body.length + " ",
                ""
        );

        outputStream.write((headers + "\r\n").getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
