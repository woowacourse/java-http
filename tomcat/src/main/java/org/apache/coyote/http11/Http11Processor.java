package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Map<String, String> CONTENT_TYPE_MAP = Map.ofEntries(
            Map.entry(".html", "text/html;charset=utf-8"),
            Map.entry(".css",  "text/css"),
            Map.entry(".js",   "application/javascript")
    );

    public static final String HEADER_CONTENT_TYPE = "Content-Type: ";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length: ";
    public static final String HEADER_LOCATION = "Location: ";
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
            String requestLine = br.readLine();
            ResourceResolutionResult resolutionResult = StaticResourceProcessor.resolveResourcePath(requestLine);
            String resourceName = resolutionResult.resource();
            String contentType = determineContentType(resourceName);
            
            URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
            if (resourceUrl == null) {
                String notFoundResponse = buildNotFoundResponse();
                sendResponse(outputStream, notFoundResponse);
                return;
            }

            if (resolutionResult.hasAuthParam()) {
                try {
                    AuthHandler.authenticate(resolutionResult.queryParams());
                    String redirectResponse = buildFoundRedirectResponse("/index.html");
                    sendResponse(outputStream, redirectResponse);
                    return;
                } catch (IllegalArgumentException e) {
                    sendUnauthorizedPageResponse(outputStream);
                    return;
                }
            }

            String response = buildResponseWithFile(HttpStatus.OK, contentType, Path.of(resourceUrl.toURI()));
            sendResponse(outputStream, response);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String determineContentType(String resourceName) {
        int lastDotIndex = resourceName.lastIndexOf('.');
        String fileExtension = "";
        if (lastDotIndex > 0) {
            fileExtension = resourceName.substring(lastDotIndex);
        }
        return CONTENT_TYPE_MAP.getOrDefault(fileExtension, "text/html;charset=utf-8");
    }

    private String buildNotFoundResponse() {
        return String.join(HTTP_LINE_SEPARATOR,
                HttpStatus.NOT_FOUND.getStatusLine(),
                HEADER_CONTENT_TYPE + "text/html;charset=utf-8",
                HEADER_CONTENT_LENGTH + "0",
                "",
                "");
    }

    private String buildFoundRedirectResponse(String location) throws IOException {
        return String.join(HTTP_LINE_SEPARATOR,
                HttpStatus.FOUND.getStatusLine(),
                HEADER_LOCATION + location,
                HEADER_CONTENT_LENGTH + "0",
                "",
                "");
    }

    private void sendUnauthorizedPageResponse(OutputStream outputStream) throws IOException, URISyntaxException {
        URI resourceUri = getClass().getClassLoader().getResource("static/401.html").toURI();
        String response = buildResponseWithFile(
                HttpStatus.UNAUTHORIZED,
                "text/html;charset=utf-8",
                Path.of(resourceUri)
        );
        sendResponse(outputStream, response);
    }

    private String buildResponseWithFile(HttpStatus status, String contentType, Path path) throws IOException {
        byte[] bodyBytes = Files.readAllBytes(path);
        return String.join(HTTP_LINE_SEPARATOR,
                status.getStatusLine(),
                HEADER_CONTENT_TYPE + contentType,
                HEADER_CONTENT_LENGTH + bodyBytes.length,
                "",
                Files.readString(path, StandardCharsets.UTF_8));
    }

    private void sendResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
