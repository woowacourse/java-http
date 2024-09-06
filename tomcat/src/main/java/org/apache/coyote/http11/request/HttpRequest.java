package org.apache.coyote.http11.request;

public class HttpRequest {

    private final HttpMethod httpMethod;

    private final HttpRequestPath httpRequestPath;

    private final QueryString queryString;

    private final HttpHeaders httpHeaders;

    public HttpRequest(HttpMethod httpMethod,
                       HttpRequestPath httpRequestPath,
                       QueryString queryString,
                       HttpHeaders httpHeaders) {
        this.httpMethod = httpMethod;
        this.httpRequestPath = httpRequestPath;
        this.queryString = queryString;
        this.httpHeaders = httpHeaders;
    }

    public String getHttpRequestPath() {
        return httpRequestPath.uri();
    }

    public String getContentType() {
        return httpHeaders.getContentType();
    }

    public String getQueryParameter(String key) {
        return queryString.getValue(key);
    }
}
