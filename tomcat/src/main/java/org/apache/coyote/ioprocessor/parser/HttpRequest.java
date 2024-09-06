package org.apache.coyote.ioprocessor.parser;

import http.HttpMethod;
import http.HttpRequestBody;
import http.HttpRequestHeaders;
import http.HttpRequestLine;
import http.requestheader.Accept;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpRequestHeaders requestHeaders;
    private final HttpRequestBody requestBody;

    public HttpRequest(HttpRequestLine requestLine, HttpRequestHeaders requestHeaders, HttpRequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public HttpRequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public String getQueryParam() {
        if (requestLine.getHttpMethod() == HttpMethod.GET) {
            return requestLine.getQueryParam();
        }
        return requestBody.getBody();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getUri().getPath();
    }

    public String getMediaType() {
        Accept acceptValue = requestHeaders.getAcceptValue();
        return acceptValue.processContentType(getPath());
    }
}
