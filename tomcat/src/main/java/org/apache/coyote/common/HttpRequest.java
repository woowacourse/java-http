package org.apache.coyote.common;

import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final RequestUri requestUri;
    private final HttpHeaders headers;

    public HttpRequest(RequestUri requestUri, HttpHeaders headers) {
        this.requestUri = requestUri;
        this.headers = headers;
    }

    public String getPath() {
        return requestUri.getHttpPath().getPath();
    }

    public List<String> getQueryString(String key) {
        return requestUri.getHttpPath().getQueryString(key);
    }

    public Map<String, List<String>> getQueryStrings() {
        return requestUri.getHttpPath().getQueryStrings();
    }

    public HttpMethod getHttpMethod() {
        return requestUri.getHttpMethod();
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }
}
