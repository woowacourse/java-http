package org.apache.coyote.http11;

public class RequestLine {

    private final Method method;
    private final String path;
    private final ProtocolVersion protocolVersion;

    public RequestLine(Method method, String path, ProtocolVersion protocolVersion) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public String toString() {
        return method + " " + path + " " + protocolVersion.getResponseHeader();
    }
}
