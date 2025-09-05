package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final OutputStream outputStream;
    private String contentType = "text/html;charset=utf-8";
    private byte[] body = new byte[0];

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void send() throws IOException {
        final String responseHeaders = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.length + " ",
                "",
                "");

        outputStream.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
