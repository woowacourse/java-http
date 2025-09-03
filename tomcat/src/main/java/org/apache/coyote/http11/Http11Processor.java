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
            String resourceName = ResourceResolver.resolveResourceName(firstLine);
            String contentType = determineContentType(resourceName);
            
            URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
            if (resourceUrl == null) {
                sendNotFoundResponse(outputStream);
                return;
            }
            
            String response = buildOkResponse(contentType, resourceUrl);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String determineContentType(String resourceName) {
        int lastDotIndex = resourceName.lastIndexOf('.');
        String fileExtension = (lastDotIndex > 0) ? resourceName.substring(lastDotIndex) : "";
        return CONTENT_TYPE_MAP.getOrDefault(fileExtension, "text/html; charset=utf-8");
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

    private String buildOkResponse(String contentType, URL resourceUrl) throws IOException, URISyntaxException {
        Path path = Path.of(resourceUrl.toURI());
        long contentLength = Files.size(path);
        String responseBody = Files.readString(path, StandardCharsets.UTF_8);

        return String.join(HTTP_LINE_SEPARATOR,
                HTTP_OK,
                HEADER_CONTENT_TYPE + contentType + " ",
                HEADER_CONTENT_LENGTH + contentLength + " ",
                "",
                responseBody);
    }
}
