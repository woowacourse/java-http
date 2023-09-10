package org.apache.coyote.http11.request;

public class RequestLine {

    private final String method;
    private final RequestUri requestUri;
    private final String protocol;

    private RequestLine(String method, RequestUri requestUri, String protocol) {
        this.method = method;
        this.requestUri = requestUri;
        this.protocol = protocol;
    }

    public static RequestLine of(String line) {
        String[] split = line.split(" ");
        return new RequestLine(split[0], new RequestUri(split[1]), split[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return requestUri.getUri();
    }

    public String getRequestUri() {
        return requestUri.getUri();
    }

    public String getProtocol() {
        return protocol;
    }
}
