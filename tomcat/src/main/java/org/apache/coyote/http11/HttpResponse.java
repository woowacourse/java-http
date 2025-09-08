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
                     byte[] body) throws IOException {
        String header = String.format(
                "HTTP/1.1 %d %s \r\n" +
                        "Content-Type: %s \r\n" +
                        "Content-Length: %d \r\n" +
                        "\r\n",
                status.getCode(),
                status.getMessage(),
                contentType,
                body.length
        );
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }

    public void sendError(HttpStatus status) throws IOException {
        String body = "<h1>" + status.getCode() + " " + status.getMessage() + "</h1>";
        String header = String.format("""
                        HTTP/1.1 %d %s\r
                        Content-Type: text/html; charset=UTF-8\r
                        Content-Length: %d\r
                        Connection: close\r
                        \r
                        """,
                status.getCode(),
                status.getMessage(),
                body.getBytes().length
        );
        outputStream.write(header.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body.getBytes());
        outputStream.flush();
    }

    @Override
    public void close() throws Exception {
        outputStream.close();
    }
}
