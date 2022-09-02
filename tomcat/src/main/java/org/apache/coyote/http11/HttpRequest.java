package org.apache.coyote.http11;

import java.util.List;

public class HttpRequest {

    private static final int REQUEST_METHOD = 0;
    private static final int REQUEST_URI = 1;
    private static final int PROTOCOL_VERSION = 2;
    private static final String REQUEST_LINE_SEPARATOR = " ";

    private final String method;
    private final String uri;
    private final String protocolVersion;
    private final HttpRequestHeader httpRequestHeader;

    public HttpRequest(String requestLine, List<String> requestHeader) {
        String[] requestLineValues = parseRequestLine(requestLine);
        this.method = requestLineValues[REQUEST_METHOD];
        this.uri = requestLineValues[REQUEST_URI];
        this.protocolVersion = requestLineValues[PROTOCOL_VERSION];
        this.httpRequestHeader = new HttpRequestHeader(requestHeader);
    }

    private String[] parseRequestLine(String requestLine) {
        return requestLine.split(REQUEST_LINE_SEPARATOR);
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
