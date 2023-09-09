package org.apache.coyote.http11.request;

import org.apache.coyote.protocol.Protocol;

public class RequestLine {

    private final RequestMethod requestMethod;
    private final RequestUri requestUri;
    private final Protocol protocol;

    public RequestLine(final RequestMethod requestMethod,
                       final RequestUri requestUri,
                       final Protocol protocol) {
        this.requestMethod = requestMethod;
        this.requestUri = requestUri;
        this.protocol = protocol;
    }

    public static RequestLine from(final String requestFirstLine) {
        final String[] requestFirstLineElements = requestFirstLine.split(" ");
        final String requestMethodValue = requestFirstLineElements[0];
        final String requestUriValue = requestFirstLineElements[1];
        final String requestProtocolValue = requestFirstLineElements[2];

        final RequestMethod requestMethod = RequestMethod.from(requestMethodValue);
        final RequestUri requestUri = RequestUri.from(requestUriValue);
        final Protocol protocol = Protocol.from(requestProtocolValue);

        return new RequestLine(requestMethod, requestUri, protocol);
    }

    public boolean isMatching(final String requestPath, final RequestMethod requestMethod) {
        return this.requestUri.getPath().equals(requestPath)
                && this.requestMethod == requestMethod;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestUri.getPath();
    }

    public String getQueryString() {
        return requestUri.getQueryString();
    }

    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "requestMethod=" + requestMethod +
                ", requestUri='" + requestUri +
                ", protocol=" + protocol +
                '}';
    }
}
