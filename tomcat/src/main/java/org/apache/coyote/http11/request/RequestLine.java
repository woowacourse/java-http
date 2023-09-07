package org.apache.coyote.http11.request;

import org.apache.coyote.protocol.Protocol;

public class RequestLine {

    private final RequestMethod requestMethod;
    private final String requestPath;
    private final Protocol protocol;

    private RequestLine(final RequestMethod requestMethod,
                       final String requestPath,
                       final Protocol protocol) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.protocol = protocol;
    }

    public static RequestLine from(final String requestFirstLine) {

        final String[] requestFirstLineElements = requestFirstLine.split(" ");
        final String requestMethodValue = requestFirstLineElements[0];
        final String requestUriValue = requestFirstLineElements[1];
        final String requestProtocolValue = requestFirstLineElements[2];

        final String path = getPath(requestUriValue);
        final RequestMethod requestMethod = RequestMethod.from(requestMethodValue);
        final Protocol protocol = Protocol.from(requestProtocolValue);

        return new RequestLine(requestMethod, path, protocol);
    }

    private static String getPath(final String requestUri) {
        if (requestUri.contains("?")) {
            return requestUri.substring(0, requestUri.indexOf("?"));
        }
        return requestUri;
    }

    public boolean isMatching(final String requestPath, final RequestMethod requestMethod) {
        return this.requestPath.equals(requestPath)
                && this.requestMethod == requestMethod;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "requestMethod=" + requestMethod +
                ", requestPath='" + requestPath + '\'' +
                ", protocol=" + protocol +
                '}';
    }
}
