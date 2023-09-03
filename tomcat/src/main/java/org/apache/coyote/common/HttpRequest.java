package org.apache.coyote.common;

public class HttpRequest {

    private final RequestUri requestUri;
    private final HttpHeaders headers;
    private final String requestBody;
    private final HttpCookie cookie;

    public HttpRequest(RequestUri requestUri, HttpHeaders headers, HttpCookie cookie, String requestBody) {
        this.requestUri = requestUri;
        this.headers = headers;
        this.cookie = cookie;
        this.requestBody = requestBody;
    }

    public String getPath() {
        return requestUri.getHttpPath().getPath();
    }

    public String getQueryString(String key) {
        return requestUri.getHttpPath().getQueryString(key);
    }

    public QueryString getQueryStrings() {
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
