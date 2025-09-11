package org.apache.coyote.http11;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private final BufferedOutputStream outputStream;
    private HttpCookie cookie;

    public HttpResponse(BufferedOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addCookie(HttpCookie cookie) {
        this.cookie = cookie;
    }

    private String buildHeader(HttpStatus status, String contentType, int contentLength, String... additionalHeaders) {
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append(String.format("HTTP/1.1 %d %s \r\n", status.getCode(), status.getMessage()));

        if (contentType != null) {
            headerBuilder.append(String.format("Content-Type: %s \r\n", contentType));
        }

        headerBuilder.append(String.format("Content-Length: %d \r\n", contentLength));

        for (String header : additionalHeaders) {
            if (header != null && !header.isEmpty()) {
                headerBuilder.append(header).append("\r\n");
            }
        }

        if (cookie != null) {
            headerBuilder.append(cookie).append("\r\n");
        }

        headerBuilder.append("\r\n");
        return headerBuilder.toString();
    }

    public void send(HttpStatus status, String contentType, byte[] body) throws IOException {
        String header = buildHeader(status, contentType, body.length);
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    public void sendRedirect(String location) throws IOException {
        String locationHeader = String.format("Location: %s", location);
        String header = buildHeader(HttpStatus.FOUND, null, 0, locationHeader);
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    public void sendError(HttpStatus status) throws IOException {
        byte[] body = getErrorBody(status);
        String header = buildHeader(status, "text/html; charset=UTF-8", body.length, "Connection: close");
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private byte[] getErrorBody(HttpStatus status) {
        String errorPage = String.format("static/%d.html", status.getCode());
        try (InputStream errorPageStream = getClass().getClassLoader().getResourceAsStream(errorPage)) {
            if (errorPageStream != null) {
                return errorPageStream.readAllBytes();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        String defaultBody = "<h1>" + status.getCode() + " " + status.getMessage() + "</h1>";
        return defaultBody.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void close() throws Exception {
        outputStream.close();
    }
}
