package org.apache.coyote.http11.response;

public class HttpResponse {
    private final HttpStatus httpStatus;
    private final ResponseHeaders responseHeaders;
    private final String body;

    public HttpResponse(HttpStatus httpStatus, ResponseHeaders responseHeaders, String body) {
        this.httpStatus = httpStatus;
        this.responseHeaders = responseHeaders;
        this.body = body;
    }

    public HttpStatus status() {
        return httpStatus;
    }

    public ResponseHeaders headers() {
        return responseHeaders;
    }

    public String body() {
        return body;
    }
}
