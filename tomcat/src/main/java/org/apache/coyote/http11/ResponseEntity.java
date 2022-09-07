package org.apache.coyote.http11;

import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.HttpStatus;

public class ResponseEntity {

    private final HttpStatus status;
    private final String body;
    private final ContentType contentType;
    private final HttpHeaders headers;

    public ResponseEntity(HttpStatus status, String body, ContentType contentType) {
        this(status, body, contentType, null);
    }

    public ResponseEntity(HttpStatus status, ContentType contentType, HttpHeaders headers) {
        this(status, null, contentType, headers);
    }

    public ResponseEntity(HttpStatus status, String body, ContentType contentType, HttpHeaders headers) {
        this.status = status;
        this.body = body;
        this.contentType = contentType;
        this.headers = headers;
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

    public HttpHeaders getHeaders() {
        return headers;
    }
}
