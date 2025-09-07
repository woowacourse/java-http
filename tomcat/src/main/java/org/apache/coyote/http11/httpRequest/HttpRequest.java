package org.apache.coyote.http11.httpRequest;

import org.apache.coyote.http11.general.HttpBody;
import org.apache.coyote.http11.general.HttpHeaders;

public class HttpRequest {

    private final HttpMethod method;
    private final String path;
    private final QueryStrings queryStrings;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(HttpMethod method, String path, QueryStrings queryStrings, HttpHeaders headers, HttpBody body) {
        this.method = method;
        this.path = path;
        this.queryStrings = queryStrings;
        this.headers = headers;
        this.body = body;
    }

    public boolean pathEquals(String path) {
        return this.path.equals(path);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return this.path;
    }

    public String getHeaderValueOf(String key) {
        return this.headers.getHeaderValueOf(key);
    }

    public String getBodyValueOf(String key) {
        return body.get(key);
    }
}
