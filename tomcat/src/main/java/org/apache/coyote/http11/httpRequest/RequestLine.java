package org.apache.coyote.http11.httpRequest;

import org.apache.coyote.http11.general.HttpProtocolVersion;

public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final QueryStrings queryStrings;
    private final HttpProtocolVersion protocolVersion;

    public RequestLine(HttpMethod method, String path, QueryStrings queryStrings, HttpProtocolVersion protocolVersion) {
        this.method = method;
        this.path = path;
        this.queryStrings = queryStrings;
        this.protocolVersion = protocolVersion;
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

    public HttpProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }
}
