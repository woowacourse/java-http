package org.apache.coyote.http11.httpRequest;

import org.apache.coyote.http11.general.HttpBody;

public class HttpRequest {

    private final HttpRequestHeaders headers;
    private final HttpBody body;

    public HttpRequest(HttpRequestHeaders headers, HttpBody body) {
        this.headers = headers;
        this.body = body;
    }

    public boolean isQueryStringsEmpty() {
        return this.headers.isQueryStringsEmpty();
    }

    public String getQueryStringValueOf(String key) {
        return headers.getQueryStringOf(key);
    }

    public boolean pathEquals(String path) {
        return this.headers.pathEquals(path);
    }

    public HttpMethod getMethod() {
        return headers.getMethod();
    }

    public String getPath() {
        return headers.getPath();
    }

    public String getBodyValueOf(String key) {
        return body.get(key);
    }
}
