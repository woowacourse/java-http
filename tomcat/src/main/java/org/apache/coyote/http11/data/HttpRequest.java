package org.apache.coyote.http11.data;

import java.util.ArrayList;
import java.util.List;

public class HttpRequest {
    private static final String SESSION_ID_KEY = "JSESSIONID";

    private final HttpMethod httpMethod;
    private final String path;
    private final HttpVersion httpVersion;
    private final String requestBody;
    private final ContentType contentType;
    private final Integer contentLength;
    private final HttpRequestParameter httpRequestParameter;
    private final List<HttpCookie> httpCookies;

    public HttpRequest(HttpMethod httpMethod, String path, HttpVersion httpVersion, String requestBody,
                       ContentType contentType, Integer contentLength, HttpRequestParameter httpRequestParameter,
                       List<HttpCookie> httpCookies) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
        this.requestBody = requestBody;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.httpRequestParameter = httpRequestParameter;
        this.httpCookies = new ArrayList<>(httpCookies);
    }

    public String getSessionId() {
        return httpCookies.stream()
                .filter(cookie -> cookie.getName().equals(SESSION_ID_KEY))
                .findAny()
                .map(HttpCookie::getValue)
                .orElse(null);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public Integer getContentLength() {
        return contentLength;
    }

    public HttpRequestParameter getHttpRequestParameter() {
        return httpRequestParameter;
    }
}
