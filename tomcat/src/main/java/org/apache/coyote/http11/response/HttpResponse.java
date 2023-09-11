package org.apache.coyote.http11.response;

import org.apache.catalina.Session;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.HttpVersion;

public class HttpResponse {

    private final HttpVersion httpVersion;
    private final Headers headers = new Headers();
    private final HttpCookie httpCookie = new HttpCookie();
    private HttpStatus httpStatus;
    private Session session;
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

    public HttpResponse setSession(final Session session) {
        this.session = session;
        return this;
    }

    public HttpResponse setCookie(final String key, final String value) {
        httpCookie.put(key, value);
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

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public Session getSession() {
        return session;
    }
}
