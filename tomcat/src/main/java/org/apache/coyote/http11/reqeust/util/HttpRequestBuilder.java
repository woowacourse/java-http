package org.apache.coyote.http11.reqeust.util;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.reqeust.HttpMethod;
import org.apache.coyote.http11.HttpProtocolVersion;
import org.apache.coyote.http11.reqeust.HttpRequest;

public class HttpRequestBuilder {

    private HttpMethod method;
    private String uri;
    private HttpProtocolVersion protocolVersion;
    private HttpHeaders headers;
    private String body;

    public HttpRequestBuilder method(final HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpRequestBuilder uri(final String uri) {
        this.uri = uri;
        return this;
    }

    public HttpRequestBuilder protocolVersion(final HttpProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
        return this;
    }

    public HttpRequestBuilder headers(final HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequestBuilder body(final String body) {
        this.body = body;
        return this;
    }

    public HttpRequest build() {
        return new HttpRequest(
                method,
                uri,
                protocolVersion,
                headers,
                body
        );
    }
}
