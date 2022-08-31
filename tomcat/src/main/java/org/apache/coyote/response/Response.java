package org.apache.coyote.response;

import org.apache.coyote.header.ContentType;
import org.apache.coyote.header.StatusCode;

public class Response {
    private final ContentType contentType;
    private final StatusCode statusCode;
    private final String body;

    public Response(final ContentType contentType, final StatusCode statusCode, final String body) {
        this.contentType = contentType;
        this.statusCode = statusCode;
        this.body = body;
    }

    public String toText() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode.toString() + " ",
                "Content-Type: " + contentType.toString() + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
