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

    public HttpResponse(BufferedOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send(HttpStatus status, String contentType, byte[] body) throws IOException {
        String header = String.format("""
                HTTP/1.1 %d %s\r
                Content-Type: %s\r
                Content-Length: %d\r
                \r
                """,
            status.getCode(),
            status.getMessage(),
            contentType,
            body.length
    );
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    public void sendRedirect(String location) throws IOException {
        String header = String.format("""
                HTTP/1.1 %d %s\r
                Location: %s\r
                Content-Length: 0\r
                \r
                """,
            HttpStatus.FOUND.getCode(),
            HttpStatus.FOUND.getMessage(),
            location
    );
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    public void sendError(HttpStatus status) throws IOException {
        byte[] body = getErrorBody(status);
        String header = String.format("""
                        HTTP/1.1 %d %s\r
                        Content-Type: text/html; charset=UTF-8\r
                        Content-Length: %d\r
                        Connection: close\r
                        \r
                        """,
                status.getCode(),
                status.getMessage(),
                body.length
        );
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    private byte[] getErrorBody(HttpStatus status) {
        System.out.println(status.getCode());
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
