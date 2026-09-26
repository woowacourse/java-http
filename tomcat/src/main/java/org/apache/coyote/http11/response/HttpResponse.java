package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpCookie;

import java.nio.file.Path;

public class HttpResponse {
    private final HttpStatus httpStatus;
    private final ResponseHeaders responseHeaders;
    private final String body;

    public HttpResponse(HttpStatus httpStatus, ResponseHeaders responseHeaders, String body) {
        this.httpStatus = httpStatus;
        this.responseHeaders = responseHeaders;
        this.body = body;
    }

    public HttpResponse(final HttpStatus httpStatus, final Path filePath, final String body,
                        final String location, final HttpCookie httpCookie) {
        this(httpStatus, new ResponseHeaders(filePath, location, httpCookie), body);
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

    public String toHttpMessage() {
        return new StringBuilder()
                .append("HTTP/1.1 ").append(status()).append(" ").append("\r\n")
                .append(headers().toHttpMessage(body().getBytes().length))
                .append("\r\n")
                .append(body())
                .toString();
    }
}
