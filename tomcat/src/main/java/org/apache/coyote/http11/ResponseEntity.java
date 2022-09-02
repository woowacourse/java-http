package org.apache.coyote.http11;

import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.headers.ContentType;

public class ResponseEntity {

    private final HttpStatus status;
    private final String body;
    private final ContentType contentType;

    public ResponseEntity(HttpStatus status, String body, ContentType contentType) {
        this.status = status;
        this.body = body;
        this.contentType = contentType;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
