package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.ContentType;
import org.apache.coyote.http11.enums.HttpStatus;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final String body;

    public HttpResponse(final HttpStatus httpStatus, final ContentType contentType, final String body) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.body = body;
    }

    public String generateResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getValue() + " OK ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
