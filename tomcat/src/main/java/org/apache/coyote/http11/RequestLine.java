package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;

public class RequestLine {

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

        final String[] split = line.split(" ");
        return new RequestLine(HttpMethod.from(split[0]), RequestPath.from(split[1]), split[2]);
    }

    public boolean hasFileExtension(final String extension) {
        return requestPath.isParamEmpty() && requestPath.contains(extension);
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
