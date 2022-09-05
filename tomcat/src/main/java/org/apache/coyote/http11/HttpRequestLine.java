package org.apache.coyote.http11;

import org.apache.coyote.exception.InvalidHttpRequestException;

public class HttpRequestLine {

    private static final int NUM_OF_ELEMENTS = 3;
    private static final int REQUEST_METHOD = 0;
    private static final int REQUEST_URI = 1;
    private static final int PROTOCOL_VERSION = 2;
    private static final String REQUEST_LINE_SEPARATOR = " ";

    private final HttpMethod method;
    private final String uri;
    private final String protocolVersion;

    public HttpRequestLine(String requestLine) {
        String[] requestLineValues = parseRequestLine(requestLine);
        validateRequestLineValues(requestLineValues);
        this.method = HttpMethod.of(requestLineValues[REQUEST_METHOD]);
        this.uri = requestLineValues[REQUEST_URI];
        this.protocolVersion = requestLineValues[PROTOCOL_VERSION];
    }

    private String[] parseRequestLine(String requestLine) {
        return requestLine.split(REQUEST_LINE_SEPARATOR);
    }

    private void validateRequestLineValues(String[] requestLineValues) {
        if (requestLineValues.length != NUM_OF_ELEMENTS) {
            throw new InvalidHttpRequestException("잘못된 형식의 Request Line입니다.");
        }
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
}
