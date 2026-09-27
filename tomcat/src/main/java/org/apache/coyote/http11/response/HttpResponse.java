package org.apache.coyote.http11.response;

import java.nio.file.Path;

public class HttpResponse {
    private HttpStatus httpStatus;
    private ResponseHeaders responseHeaders;
    private String body;

    public HttpResponse() {
        this.responseHeaders = new ResponseHeaders();
        this.body = "";
    }

    public HttpResponse(HttpStatus httpStatus, ResponseHeaders responseHeaders, String body) {
        this.httpStatus = httpStatus;
        this.responseHeaders = responseHeaders;
        this.body = body;
    }

    public HttpResponse(final HttpStatus httpStatus, final Path filePath, final String body,
                        final String location) {
        this(httpStatus, new ResponseHeaders(filePath, location), body);
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

    public void set(final HttpStatus httpStatus, final Path filePath, final String body,
                    final String location) {
        this.httpStatus = httpStatus;
        this.responseHeaders = new ResponseHeaders(filePath, location);
        this.body = body;
    }

    public void copyFrom(final HttpResponse response) {
        this.httpStatus = response.status();
        this.responseHeaders = response.headers();
        this.body = response.body();
    }

    public String toHttpMessage() {
        return new StringBuilder()
                .append("HTTP/1.1 ").append(status()).append(" ").append("\r\n")
                .append(headers().toHttpMessage(body().getBytes().length))
                .append("\r\n")
                .append(body())
                .toString();
    }
}
