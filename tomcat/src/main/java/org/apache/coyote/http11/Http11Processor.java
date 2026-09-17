package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_RESOURCE_ROOT = "static";
    private static final String INDEX_URI = "/index.html";
    private static final String CSS_URI = "/css/styles.css";
    private static final String SCRIPT_URI = "/js/scripts.js";
    private static final String CHART_AREA_URI = "/assets/chart-area.js";
    private static final String CHART_BAR_URI = "/assets/chart-bar.js";
    private static final String CHART_PIE_URI = "/assets/chart-pie.js";

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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }
            final String[] requestParts = requestLine.split(" ");
            final String method = requestParts[0];
            final String requestUri = requestParts[1];
            final String version = requestParts[2];
            final Map<String, String> headers = readHeaders(reader);

            byte[] responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            String contentType = getContentType(requestUri);
            if (requestUri.equals(INDEX_URI)) {
                final Path path = Path.of(getResourcePath(STATIC_RESOURCE_ROOT + requestUri));
                responseBody = Files.readAllBytes(path);
            }

            if (requestUri.equals(CSS_URI)) {
                final Path path = Path.of(getResourcePath(STATIC_RESOURCE_ROOT + requestUri));
                responseBody = Files.readAllBytes(path);
            }

            if (requestUri.equals(SCRIPT_URI)) {
                final Path path = Path.of(getResourcePath(STATIC_RESOURCE_ROOT + requestUri));
                responseBody = Files.readAllBytes(path);
            }

            if (requestUri.equals(CHART_AREA_URI)) {
                final Path path = Path.of(getResourcePath(STATIC_RESOURCE_ROOT + requestUri));
                responseBody = Files.readAllBytes(path);
            }

            if (requestUri.equals(CHART_BAR_URI)) {
                final Path path = Path.of(getResourcePath(STATIC_RESOURCE_ROOT + requestUri));
                responseBody = Files.readAllBytes(path);
            }

            if (requestUri.equals(CHART_PIE_URI)) {
                final Path path = Path.of(getResourcePath(STATIC_RESOURCE_ROOT + requestUri));
                responseBody = Files.readAllBytes(path);
            }

            final String responseHeader = createResponseHeader(version, contentType, responseBody.length);

            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final String[] header = line.split(":", 2);
            final String name = header[0].trim();
            final String value = header[1].trim();
            headers.put(name, value);
        }
        return headers;
    }

    private String createResponseHeader(final String version, String contentType, final int contentLength) {
        return String.join("\r\n",
                version + " 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + contentLength + " ",
                "",
                "");
    }

    private String getResourcePath(String path) {
        return getClass().getClassLoader()
                .getResource(path)
                .getPath();
    }

    private String getContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }
}
