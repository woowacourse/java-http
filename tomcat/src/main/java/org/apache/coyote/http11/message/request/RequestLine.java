package org.apache.coyote.http11.message.request;

import java.util.Map;

public class RequestLine {
    private final HttpMethod method;
    private final RequestUri requestUri;
    private final String version;

    public RequestLine(HttpMethod method, RequestUri requestUri, String version) {
        this.method = method;
        this.requestUri = requestUri;
        this.version = version;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return requestUri.getPath();
    }

    public Map<String, String> getQueryParams() {
        return requestUri.getQueryParams();
    }

    public String getVersion() {
        return version;
    }
}
