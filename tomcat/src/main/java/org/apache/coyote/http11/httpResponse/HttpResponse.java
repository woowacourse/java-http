package org.apache.coyote.http11.httpResponse;

import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final String status;
    private final String contentType;
    private final byte[] body;

    public HttpResponse(String status, String contentType, String body) {
        this.status = status;
        this.contentType = contentType;
        if (body == null) {
            this.body = new byte[0];
        } else {
            this.body = body.getBytes(StandardCharsets.UTF_8);
        }
    }

    public String toString() {
        return String.join("\r\n",
            "HTTP/1.1 " + status,
            "Content-Type: " + contentType,
            "Content-Length: " + body.length,
            "",
            new String(body, StandardCharsets.UTF_8));
    }
}
