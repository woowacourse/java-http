package org.apache.coyote.http11.request;

import java.net.URI;
import org.apache.coyote.http11.common.Method;
import org.apache.coyote.http11.common.Protocol;

public class RequestLine {

    private final Method method;
    private final URI uri;
    private final Protocol protocol;

    private RequestLine(
            final Method method,
            final URI uri,
            final Protocol protocol
    ) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
    }

    public static RequestLine of(
            final String methodName,
            final String requestUri,
            final String protocolName
    ) {
        final var method = Method.find(methodName)
                .orElseThrow(() -> new IllegalArgumentException("invalid method"));
        final var uri = URI.create(requestUri);
        final var protocol = Protocol.find(protocolName)
                .orElseThrow(() -> new IllegalArgumentException("invalid protocol"));

        return new RequestLine(
                method,
                uri,
                protocol
        );
    }

    public String getPath() {
        return uri.getPath();
    }

    public Method getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method=" + method +
                ", uri=" + uri +
                ", protocol=" + protocol +
                '}';
    }

}
