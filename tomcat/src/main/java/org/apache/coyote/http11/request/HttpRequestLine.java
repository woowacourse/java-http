package org.apache.coyote.http11.request;

import org.apache.coyote.http11.header.HttpVersion;

public class HttpRequestLine {

    private static final String DELIMITER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int PROTOCOL_VERSION_INDEX = 2;

    private final HttpMethod method;
    private final HttpPath path;
    private final HttpVersion version;

    public HttpRequestLine(final HttpMethod method, final HttpPath path, final HttpVersion version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpRequestLine from(final String line) {
        final String[] values = line.split(DELIMITER);
        return new HttpRequestLine(HttpMethod.from(values[METHOD_INDEX]),
                HttpPath.from(values[PATH_INDEX]),
                HttpVersion.from(values[PROTOCOL_VERSION_INDEX]));
    }

    public boolean isSameMethod(final HttpMethod method) {
        return this.method == method;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path.getPath();
    }

    public HttpVersion getVersion() {
        return version;
    }
}
