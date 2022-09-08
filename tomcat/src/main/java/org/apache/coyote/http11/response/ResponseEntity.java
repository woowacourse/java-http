package org.apache.coyote.http11.response;

public class ResponseEntity {

    private final HttpStatus status;
    private final String body;

    public ResponseEntity(HttpStatus status, String body) {
        this.status = status;
        this.body = body;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }
}
