package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;

public class RequestLine {

    private static final String WHITESPACE_SEPARATOR = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int PROTOCOL_VERSION_INDEX = 2;

    private final HttpMethod method;
    private final String path;
    private final String protocolVersion;

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(WHITESPACE_SEPARATOR);
        this.method = HttpMethod.valueOf(parts[HTTP_METHOD_INDEX]);
        this.path = parts[PATH_INDEX];
        this.protocolVersion = parts[PROTOCOL_VERSION_INDEX];
    }

    public boolean isGet() {
        return method.isGet();
    }

    public boolean isPost() {
        return method.isPost();
    }

    public boolean isSamePath(final String uri) {
        return path.equals(uri);
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
