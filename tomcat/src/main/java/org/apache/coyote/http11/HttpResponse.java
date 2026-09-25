package org.apache.coyote.http11;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final StatusLine statusLine;
    private final HttpHeaders headers;
    private final byte[] body;

    public HttpResponse(StatusLine statusLine, HttpHeaders headers, byte[] body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body.clone();
    }

    public void write(OutputStream outputStream) throws IOException {
        StringBuilder head = new StringBuilder(statusLine.toLine()).append("\r\n");

        for (String line : headers.toLines()) {
            head.append(line).append("\r\n");
        }
        head.append("\r\n");

        outputStream.write(head.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
