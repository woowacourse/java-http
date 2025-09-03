package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    // Resource constants
    public static final String STATIC_RESOURCE_PREFIX = "static";

    private static final Map<String, String> CONTENT_TYPE_MAP = Map.ofEntries(
            Map.entry(".html", "text/html; charset=utf-8"),
            Map.entry(".css",  "text/css"),
            Map.entry(".js",   "application/javascript")
    );

    public static final String HTTP_OK = "HTTP/1.1 200 OK ";
    public static final String HTTP_NOT_FOUND = "HTTP/1.1 404 Not Found";

    public static final String HEADER_CONTENT_TYPE = "Content-Type: ";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length: ";
    public static final String HTTP_LINE_SEPARATOR = "\r\n";

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
             final var br = new BufferedReader(new InputStreamReader(inputStream))) {

            String firstLine = br.readLine();
            String[] splitFirstLine = firstLine.split(" ");
            String requestUri = splitFirstLine[1];
            String resource = processRequestResource(requestUri);
            String resourceName = STATIC_RESOURCE_PREFIX + resource;
            String contentType = determineContentType(resourceName);
            
            URL resourceUrl = loadResource(resourceName);
            if (resourceUrl == null) {
                sendNotFoundResponse(outputStream);
                return;
            }
            
            String responseBody = readResourceContent(resourceUrl);
            long contentLength = calculateContentLength(resourceUrl);
            String response = buildOkResponse(contentType, contentLength, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String processRequestResource(String requestUri) {
        return QueryStringParser.parseAndProcessQuery(requestUri);
    }

    private String determineContentType(String resourceName) {
        String fileExtension = getFileExtension(resourceName);
        return CONTENT_TYPE_MAP.getOrDefault(fileExtension, "text/html; charset=utf-8");
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }

    private URL loadResource(String resourceName) {
        URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
        if (resourceUrl == null) {
            resourceUrl = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        }
        return resourceUrl;
    }

    private void sendNotFoundResponse(java.io.OutputStream outputStream) throws IOException {
        final var notFoundResponse = String.join(HTTP_LINE_SEPARATOR,
                HTTP_NOT_FOUND,
                HEADER_CONTENT_TYPE + "text/html; charset=utf-8",
                HEADER_CONTENT_LENGTH + "0",
                "",
                "");
        outputStream.write(notFoundResponse.getBytes());
        outputStream.flush();
    }

    private String readResourceContent(URL resourceUrl) throws IOException, URISyntaxException {
        Path path = Path.of(resourceUrl.toURI());
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private long calculateContentLength(URL resourceUrl) throws IOException, URISyntaxException {
        Path path = Path.of(resourceUrl.toURI());
        return Files.size(path);
    }

    private String buildOkResponse(String contentType, long contentLength, String responseBody) {
        return String.join(HTTP_LINE_SEPARATOR,
                HTTP_OK,
                HEADER_CONTENT_TYPE + contentType + " ",
                HEADER_CONTENT_LENGTH + contentLength + " ",
                "",
                responseBody);
    }
}
