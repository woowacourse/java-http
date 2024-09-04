package org.apache.coyote.http11.domain;

import org.apache.coyote.http11.domain.protocolVersion.ProtocolVersion;

public class RequestLine {

    private final RequestMethod requestMethod;
    private final RequestPath requestPath;
    private final ProtocolVersion protocolVersion;

    public RequestLine(RequestMethod requestMethod, RequestPath requestPath, ProtocolVersion protocolVersion) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.protocolVersion = protocolVersion;
    }

    public String getRequestPathValue() {
        return requestPath.getPath();
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }
}
