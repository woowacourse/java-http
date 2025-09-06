package org.apache.coyote.http11;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpResponse implements AutoCloseable {

    private final BufferedOutputStream outputStream;

    public HttpResponse(BufferedOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send(HttpStatus status, String contentType,
                     byte[] body, boolean keepAlive) throws IOException {
        String head = "HTTP/1.1 " + status.getCode() + " " + status.getMessage() + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + (keepAlive ? "Connection: keep-alive\r\n" : "Connection: close\r\n")
                + "\r\n";
        outputStream.write(head.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    public void sendError(HttpStatus status, byte[] body) throws IOException {
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

    @Override
    public void close() throws Exception {
        outputStream.close();
    }
}
