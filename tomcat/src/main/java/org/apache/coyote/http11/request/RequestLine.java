package org.apache.coyote.http11.request;

import java.net.URI;

public class RequestLine {

    private final Method method;
    private final URI uri;
    private final HttpProtocol protocol;

    public RequestLine(final Method method, final URI uri, final HttpProtocol protocol) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
    }

    public Method getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public String getUriPath() {
        return uri.getPath();
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }

    public String getProtocolMessage() {
        return protocol.getHttpMessage();
    }
}
