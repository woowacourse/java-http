package org.apache.coyote.http11;

import java.util.List;

public class HttpRequest {

    private final String method;
    private final String uri;
    private final String protocolVersion;
    private final HttpRequestHeader httpRequestHeader;

    public HttpRequest(String requestLine, List<String> requestHeader) {
        String[] requestLineValues = parseRequestLine(requestLine);
        this.method = requestLineValues[0];
        this.uri = requestLineValues[1];
        this.protocolVersion = requestLineValues[2];
        this.httpRequestHeader = new HttpRequestHeader(requestHeader);
    }

    private String[] parseRequestLine(String requestLine) {
        return requestLine.split(" ");
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public HttpRequestHeader getHttpRequestHeader() {
        return httpRequestHeader;
    }
}
