package org.apache.coyote.http11.httpResponse;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.general.ContentType;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final byte[] body;

    public HttpResponse(HttpStatus httpStatus, ContentType contentType, String body) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        if (body == null) {
            this.body = new byte[0];
        } else {
            this.body = body.getBytes(StandardCharsets.UTF_8);
        }
    }

    public String toString() {
        return String.join("\r\n",
            "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.getMessage(),
            "Content-Type: " + contentType.getValueWithUtf8Charset(),
            "Content-Length: " + body.length,
            "",
            new String(body, StandardCharsets.UTF_8));
    }
}
