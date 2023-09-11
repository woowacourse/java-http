package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.HttpBody;
import org.apache.coyote.http11.common.HttpVersion;

public class HttpRequest {

    private final HttpStartLine httpStartLine;
    private final HttpRequestHeaders httpRequestHeaders;
    private final HttpBody httpBody;

    public HttpRequest(
            HttpRequestHeaders httpRequestHeaders,
            HttpStartLine httpStartLine,
            HttpBody httpBody
    ) {
        this.httpStartLine = httpStartLine;
        this.httpRequestHeaders = httpRequestHeaders;
        this.httpBody = httpBody;
    }

    public boolean hasCookie() {
        return httpRequestHeaders.hasCookie();
    }

    public boolean hasQueryString() {
        return httpStartLine.hasQueryString();
    }

    public HttpMethod getHttpMethod() {
        return httpStartLine.getHttpMethod();
    }

    public boolean isGetMethod() {
        return httpStartLine.getHttpMethod() == HttpMethod.GET;
    }

    public boolean isPostMethod() {
        return httpStartLine.getHttpMethod() == HttpMethod.POST;
    }

    public HttpStartLine getHttpStartLine() {
        return httpStartLine;
    }

    public HttpRequestHeaders getHttpRequestHeaders() {
        return httpRequestHeaders;
    }

    public HttpVersion getHttpVersion() {
        return httpStartLine.getHttpVersion();
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }

    public String getNativePath() {
        return httpStartLine.getNativeUriPath();
    }

    public Cookie getCookie(String key) {
        return httpRequestHeaders.getCookie(key);
    }

    public QueryString getQueryString() {
        return httpStartLine.getQueryString();
    }

}
