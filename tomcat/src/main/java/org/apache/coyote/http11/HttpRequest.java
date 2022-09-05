package org.apache.coyote.http11;

import java.util.List;

import org.apache.coyote.exception.InvalidRequestLineFormException;

public class HttpRequest {

    private static final int REQUEST_METHOD = 0;
    private static final int REQUEST_URI = 1;
    private static final int PROTOCOL_VERSION = 2;
    private static final String REQUEST_LINE_SEPARATOR = " ";

    private final HttpMethod method;
    private final String uri;
    private final String protocolVersion;
    private final HttpRequestHeader httpRequestHeader;

    public HttpRequest(String requestLine, List<String> requestHeader) {
        String[] requestLineValues = parseRequestLine(requestLine);
        validateRequestLineValues(requestLineValues);
        this.method = HttpMethod.of(requestLineValues[REQUEST_METHOD]);
        this.uri = requestLineValues[REQUEST_URI];
        this.protocolVersion = requestLineValues[PROTOCOL_VERSION];
        this.httpRequestHeader = new HttpRequestHeader(requestHeader);
    }

    private void validateRequestLineValues(String[] requestLineValues) {
        if (requestLineValues.length != 3) {
            throw new InvalidRequestLineFormException();
        }
    }

    private String[] parseRequestLine(String requestLine) {
        return requestLine.split(REQUEST_LINE_SEPARATOR);
    }

    public HttpMethod getMethod() {
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
