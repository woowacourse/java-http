package org.apache.coyote.http11;

public class HttpRequest {

    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    public HttpRequest(HttpRequestHeader header, HttpRequestBody body) {
        this.header = header;
        this.body = body;
    }

    public HttpRequestHeader getHeader() {
        return header;
    }

    public HttpRequestBody getBody() {
        return body;
    }

    public String getQueryStringValue(String key) {
        return header.getQueryStringValue(key);
    }
}
