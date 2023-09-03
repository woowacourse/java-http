package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpVersion;

public class RequestLine {
    private static final String DELIMITER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private final HttpMethod httpMethod;
    private final RequestPath requestPath;
    private final HttpVersion httpVersion;

    private RequestLine(final HttpMethod httpMethod, final RequestPath requestPath,
                        final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.httpVersion = httpVersion;
    }

    public static RequestLine convert(String line) {
        final String[] splitLine = line.split(DELIMITER);
        final HttpMethod httpMethod = HttpMethod.findByName(splitLine[METHOD_INDEX]);
        final RequestPath requestPath = RequestPath.from(splitLine[PATH_INDEX]);
        final HttpVersion httpVersion = HttpVersion.findByVersion(splitLine[VERSION_INDEX]);
        return new RequestLine(httpMethod, requestPath, httpVersion);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestPath() {
        return requestPath.getPath();
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
