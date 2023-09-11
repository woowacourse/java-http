package org.apache.coyote.http11.request;

import nextstep.jwp.exception.UncheckedServletException;

public class RequestLine {

    private static final String BLANK_REGEX = " ";
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final RequestPath requestPath;
    private final String protocolVersion;

    public RequestLine(final HttpMethod httpMethod, final RequestPath requestPath, final String protocolVersion) {
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine from(final String line) {
        if (line == null) {
            throw new UncheckedServletException("request가 존재하지 않습니다.");
        }

        final String[] split = line.split(BLANK_REGEX);
        return new RequestLine(
                HttpMethod.from(split[METHOD_INDEX]),
                RequestPath.from(split[PATH_INDEX]),
                split[VERSION_INDEX]
        );
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestPath getRequestPath() {
        return requestPath;
    }

    public String getRequestUri() {
        return requestPath.getResource();
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
