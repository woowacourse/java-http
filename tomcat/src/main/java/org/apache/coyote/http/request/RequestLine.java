package org.apache.coyote.http.request;

public class RequestLine {

    private final String method;
    private final String endpoint;
    private final String protocolVersion;

    public RequestLine(String method, String endpoint, String protocolVersion) {
        this.method = method;
        this.endpoint = endpoint;
        this.protocolVersion = protocolVersion;
    }

    public String getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String toString() {
        return method + " " + endpoint + " " + protocolVersion;
    }
}
