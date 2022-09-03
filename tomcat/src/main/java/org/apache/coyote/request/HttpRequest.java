package org.apache.coyote.request;

import org.apache.coyote.support.HttpMethod;
import org.apache.coyote.support.HttpVersion;

public class HttpRequest {

    private final HttpMethod method;
    private final String uri;
    private final HttpVersion version;
    private final Parameters parameters;
    private final RequestHeaders headers;
    private final String body;

    public HttpRequest(StartLine startLine, RequestHeaders headers, String body) {
        this.method = startLine.getMethod();
        this.uri = startLine.getUri();
        this.version = startLine.getVersion();
        this.headers = headers;
        this.parameters = startLine.getParameters();
        this.body = body;
    }

    public boolean isMethodOf(HttpMethod method) {
        return this.method.equals(method);
    }

    public String getUri() {
        return uri;
    }

    public Parameters getParameters() {
        return parameters;
    }
}
