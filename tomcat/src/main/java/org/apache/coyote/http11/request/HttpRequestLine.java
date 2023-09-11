package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpProtocol;

public class HttpRequestLine {
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static final String START_LINE_DELIMITER = " ";

    private final HttpMethod method;
    private final HttpPath path;
    private final HttpProtocol protocol;

    private HttpRequestLine(final HttpMethod method, final HttpPath path, final HttpProtocol protocol) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
    }

    public static HttpRequestLine from(final String startLine) {
        final String[] startLines = startLine.split(START_LINE_DELIMITER);
        final HttpMethod method = HttpMethod.from(startLines[METHOD_INDEX]);
        final HttpPath path = HttpPath.from(startLines[PATH_INDEX]);
        final HttpProtocol protocol = HttpProtocol.from(startLines[PROTOCOL_INDEX]);
        return new HttpRequestLine(method, path, protocol);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPathValue() {
        return path.getPath();
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }
}
