package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> queryString;
    private final String httpVersion;
    private final Map<String, String> httpRequestHeaders;
    private final Map<String, String> requestBody;
    private final HttpCookie httpCookie;

    public HttpRequest(
            final String method,
            final String path,
            final Map<String, String> queryString,
            final String httpVersion,
            final Map<String, String> httpRequestHeaders,
            final Map<String, String> requestBody,
            final HttpCookie httpCookie
    ) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.httpVersion = httpVersion;
        this.httpRequestHeaders = httpRequestHeaders;
        this.requestBody = requestBody;
        this.httpCookie = httpCookie;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHttpRequestHeaders() {
        return httpRequestHeaders;
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }
}
