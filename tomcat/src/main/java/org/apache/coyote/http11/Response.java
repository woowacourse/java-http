package org.apache.coyote.http11;

public class Response {

    private final HttpStatus httpStatus;
    private final String content;

    public Response(final HttpStatus httpStatus, final String content) {
        this.httpStatus = httpStatus;
        this.content = content;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getContent() {
        return content;
    }
}
