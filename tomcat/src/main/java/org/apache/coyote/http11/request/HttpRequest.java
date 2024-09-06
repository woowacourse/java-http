package org.apache.coyote.http11.request;

public class HttpRequest {

    private final HttpMethod httpMethod;

    private final HttpRequestPath httpRequestPath;

    private final QueryString queryString;

    private final HttpRequestHeaders httpRequestHeaders;

    public HttpRequest(HttpMethod httpMethod,
                       HttpRequestPath httpRequestPath,
                       QueryString queryString,
                       HttpRequestHeaders httpRequestHeaders) {
        this.httpMethod = httpMethod;
        this.httpRequestPath = httpRequestPath;
        this.queryString = queryString;
        this.httpRequestHeaders = httpRequestHeaders;
    }

    public String getHttpRequestPath() {
        return httpRequestPath.uri();
    }

    public String getContentType() {
        return httpRequestHeaders.getContentType();
    }

    public String getQueryParameter(String key) {
        return queryString.getValue(key);
    }
}
