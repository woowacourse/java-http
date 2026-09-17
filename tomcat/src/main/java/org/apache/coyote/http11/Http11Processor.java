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
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String NOT_FOUND_RESOURCE_PATH = "static/404.html";
    private static final String DEFAULT_RESOURCE_PATH = "static/index.html";

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
            final var resourcePath = extractResourcePath(reader.readLine());

            if ("/".equals(resourcePath)) {
                writeResource(outputStream, DEFAULT_RESOURCE_PATH);
                return;
            }
            writeResource(outputStream, STATIC_RESOURCE_PATH + appendHtmlExtension(resourcePath));
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String extractResourcePath(final String requestLine) {
        String uri = requestLine.split(" ")[1];
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }

        String path = uri.substring(0, index);
        Map<String, String> queryParams = parseQueries(uri.substring(index + 1));
        InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .ifPresent(user -> {
                    if (user.checkPassword(queryParams.get("password"))) {
                        log.info("user : {}", user);
                    }
                });
        return path;
    }

    private static Map<String, String> parseQueries(String queryString) {
        String[] queries = queryString.split("&");
        Map<String, String> params = new HashMap<>();
        for (String query : queries) {
            String[] pair = query.split("=");
            params.put(pair[0], pair[1]);
        }
        return params;
    }

    private void writeResource(
            final OutputStream outputStream,
            final String resourcePath
    ) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                writeNotFoundResponse(outputStream);
                return;
            }

            final var contentType = MimeTypeResolver.resolve(resourcePath);
            writeResponse(outputStream, contentType, "200 OK", resource.readAllBytes());
        }
    }

    private void writeNotFoundResponse(final OutputStream outputStream) throws IOException {
        try (InputStream resource = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(NOT_FOUND_RESOURCE_PATH),
                "404 페이지를 찾을 수 없습니다."
        )) {
            writeResponse(outputStream, "text/html", "404 Not Found", resource.readAllBytes());
        }
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String contentType,
            final String status,
            final byte[] responseBody
    ) throws IOException {
        final var responseHeader = String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + toContentTypeHeader(contentType) + " ",
                "Content-Length: " + responseBody.length + " ",
                "",
                "");

        outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private String toContentTypeHeader(final String contentType) {
        if (contentType.startsWith("text/")) {
            return contentType + ";charset=utf-8";
        }
        return contentType;
    }

    private static String appendHtmlExtension(final String resourcePath) {
        final var fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        if (fileName.contains(".")) {
            return resourcePath;
        }
        return resourcePath + ".html";
    }
}
