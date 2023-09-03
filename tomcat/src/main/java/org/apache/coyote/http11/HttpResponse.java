package org.apache.coyote.http11;

public class HttpResponse {

    private final String body;
    private final HttpStatus httpStatus;
    private final ContentType contentType;

    public HttpResponse(String body, HttpStatus httpStatus, ContentType contentType) {
        this.body = body;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
    }

    public String getBody() {
        return body;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getContentType() {
        return contentType.getType();
    }
}
