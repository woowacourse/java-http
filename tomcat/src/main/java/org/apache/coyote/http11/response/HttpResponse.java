package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.HttpVersion;

public class HttpResponse {

    private final HttpVersion httpVersion;
    private final Headers headers = new Headers();
    private HttpStatus httpStatus;
    private String redirect;

    public HttpResponse(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public HttpResponse addHeader(final String key, final String value) {
        headers.addHeader(key, value);
        return this;
    }

    public HttpResponse setHttpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponse sendRedirect(final String url) {
        this.redirect = url;
        return this;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public Headers getHeaders() {
        return headers;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getRedirect() {
        return redirect;
    }
}
