package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpRequestHeaders httpRequestHeaders;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.httpRequestLine = new HttpRequestLine(bufferedReader);
        this.httpRequestHeaders = new HttpRequestHeaders(bufferedReader);
        this.httpRequestBody = new HttpRequestBody(bufferedReader, httpRequestHeaders);
    }

    public boolean containsCookie() {
        return containsHeader("Cookie");
    }

    public boolean containsFormData() {
        return containsHeader("Content-Type") && getHeader("Content-Type")
                .contains("application/x-www-form-urlencoded");
    }

    public HttpMethod getHttpMethod() {
        return httpRequestLine.getHttpMethod();
    }

    public String getUrl() {
        return httpRequestLine.getUrl();
    }

    public Map<String, String> getQueryString() {
        return httpRequestLine.getQueryString();
    }

    public HttpVersion getHttpVersion() {
        return httpRequestLine.getHttpVersion();
    }

    public boolean containsHeader(String key) {
        return httpRequestHeaders.containsKey(key);
    }

    public String getHeader(String key) {
        return httpRequestHeaders.get(key);
    }

    public String getBody() {
        return httpRequestBody.getHttpRequestBody();
    }
}
