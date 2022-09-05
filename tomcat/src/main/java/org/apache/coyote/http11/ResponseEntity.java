package org.apache.coyote.http11;

public class ResponseEntity {

    private final StatusCode statusCode;
    private final String path;
    private String body;

    public ResponseEntity(final StatusCode statusCode, final String path) {
        this.statusCode = statusCode;
        this.path = path;
    }

    public ResponseEntity body(String body) {
        this.body = body;
        return this;
    }

    public String getResponse() {
        final ResponseHeader responseHeader = new ResponseHeader(path);
        return String.join("\r\n", responseHeader.getHeader(statusCode, body), body);
    }
}
