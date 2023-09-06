package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpVersion;

public class StartLine {

    private static final String SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod method;
    private final String uri;
    private final HttpVersion version;

    private StartLine(final HttpMethod method, final String uri, final HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public static StartLine from(final String request) {
        final String[] startLine = request.split(SEPARATOR);
        final HttpMethod httpMethod = HttpMethod.findBy(startLine[METHOD_INDEX]);
        final HttpVersion httpVersion = HttpVersion.findBy(startLine[VERSION_INDEX]);

        return new StartLine(httpMethod, startLine[URI_INDEX], httpVersion);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public HttpVersion getVersion() {
        return version;
    }
}
