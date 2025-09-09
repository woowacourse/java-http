package org.apache.coyote.http11.httpRequest;

import org.apache.coyote.http11.general.HttpBody;
import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.general.HttpProtocolVersion;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, HttpBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public boolean pathEquals(String path) {
        return this.requestLine.pathEquals(path);
    }

    public HttpProtocolVersion getProtocolVersion() {
        return this.requestLine.getProtocolVersion();
    }

    public HttpMethod getMethod() {
        return this.requestLine.getMethod();
    }

    public String getPath() {
        return this.requestLine.getPath();
    }

    public String getHeaderValueOf(String key) {
        return this.headers.getHeaderValueOf(key);
    }

    public String getBodyValueOf(String key) {
        return body.get(key);
    }
}
