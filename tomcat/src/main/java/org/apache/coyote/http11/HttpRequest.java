package org.apache.coyote.http11;

import java.util.List;

public class HttpRequest {

    final String requestMethod;
    final String requestUri;
    final String protocolVersion;

    public HttpRequest(final List<String> request) {
        String requestLine = request.get(0);
        String[] s = requestLine.split(" ");
        this.requestMethod = s[0];
        this.requestUri = s[1];
        this.protocolVersion = s[2];
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestMethod='" + requestMethod + '\'' +
                ", requestUri='" + requestUri + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                '}';
    }
}
