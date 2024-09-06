package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String requestUrl;
    private final String protocol;

    public RequestLine(String method, String requestUrl, String protocol) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.protocol = protocol;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getProtocol() {
        return protocol;
    }
}
