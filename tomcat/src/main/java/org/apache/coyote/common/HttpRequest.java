package org.apache.coyote.common;

import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final RequestUri requestUri;
    private final HttpHeaders headers;
    private final String requestBody;

    public HttpRequest(RequestUri requestUri, HttpHeaders headers, String requestBody) {
        this.requestUri = requestUri;
        this.headers = headers;
        this.requestBody = requestBody;
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

    public String getRequestBody() {
        return requestBody;
    }
}
