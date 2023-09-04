package org.apache.coyote.http.vo;

import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;

public class HttpRequest {

    private final HttpMethod method;
    private final Url url;
    private final HttpHeaders headers;
    private final HttpBody body;
    private final Cookie cookie;

    private HttpRequest(
            final HttpMethod method,
            final Url url,
            final HttpHeaders headers,
            final HttpBody body
    ) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
        this.cookie = parseCookie();
    }

    private Cookie parseCookie() {
        String rawCookie = this.headers.getRecentHeaderValue(HttpHeader.COOKIE);
        if (rawCookie != null) {
            return Cookie.from(rawCookie);
        }

        return Cookie.emptyCookie();
    }

    public boolean isContainsSubStringInUrl(final String subString) {
        return url.isContainSubString(subString);
    }

    public boolean isRequestMethodOf(final HttpMethod method) {
        return this.method == method;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Url getUrl() {
        return url;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public boolean isUrl(final Url url) {
        return this.url.isSameUrl(url);
    }

    public boolean hasQueryStringOf(final String queryString) {
        return url.hasQueryStringOf(queryString);
    }

    public String getQueryStringValue(final String queryString) {
        return url.getQueryStringOf(queryString);
    }

    public boolean hasBodyValueOf(final String body) {
        return this.body.hasBody(body);
    }

    public String getBodyValueOf(final String body) {
        return this.body.getValue(body);
    }

    public HttpBody getBody() {
        return body;
    }

    public boolean hasCookie(final String cookie) {
        return this.cookie.hasCookie(cookie);
    }

    public String getCookie(final String cookie) {
        return this.cookie.getCookie(cookie);
    }

    public static class Builder {
        private HttpMethod method;
        private Url url;
        private HttpHeaders headers;
        private HttpBody body;

        public Builder httpMethod(final HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder url(final Url url) {
            this.url = url;
            return this;
        }

        public Builder headers(final HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(final HttpBody body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(method, url, headers, body);
        }
    }
}
