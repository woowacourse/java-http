package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpResponse {

    private final HttpStatus status;
    private final String contentType;
    private final String body;

    public HttpResponse(HttpStatus status, String contentType, String body) {
        this.status = status;
        this.contentType = contentType;
        this.body = body;
    }

    public byte[] getBytes() {
        final String message = String.join("\r\n",
                "HTTP/1.1 " + status.getCode() + " " + status.getReasonPhrase() + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes(UTF_8).length + " ",
                "",
                body);
        return message.getBytes(UTF_8);
    }
}
