package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequestStartLine {

    private final HttpRequestMethod httpRequestMethod;
    private final String requestURI;
    private final String httpVersion;
    private final Map<String, String> queryParams;

    public HttpRequestStartLine(
            final HttpRequestMethod httpRequestMethod,
            final String requestURI,
            final String httpVersion,
            final Map<String, String> queryParams
    ) {
        this.httpRequestMethod = httpRequestMethod;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
        this.queryParams = queryParams;
    }

    public HttpRequestMethod getHttpRequestMethod() {
        return httpRequestMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
