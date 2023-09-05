package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.common.HttpMethod;

public class HttpRequestLine {

    private final HttpMethod httpMethod;
    private final String requestURI;
    private final String httpVersion;
    private final Map<String, String> queryParams;

    public HttpRequestLine(
            final HttpMethod httpMethod,
            final String requestURI,
            final String httpVersion,
            final Map<String, String> queryParams
    ) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
        this.queryParams = queryParams;
    }

    public HttpMethod getHttpRequestMethod() {
        return httpMethod;
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
