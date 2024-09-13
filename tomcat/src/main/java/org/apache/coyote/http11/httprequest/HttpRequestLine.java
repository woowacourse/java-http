package org.apache.coyote.http11.httprequest;

import org.apache.coyote.http11.HttpMethod;

public class HttpRequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int REQUEST_LINE_MIN_LENGTH = 3;
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod method;
    private final String path;
    private final String version;

    public HttpRequestLine(String requestLine) {
        validateRequestLine(requestLine);
        String[] headerFirstLine = requestLine.split(REQUEST_LINE_DELIMITER);
        this.method = HttpMethod.getHttpMethod(headerFirstLine[METHOD_INDEX]);
        this.path = headerFirstLine[PATH_INDEX];
        this.version = headerFirstLine[VERSION_INDEX];
    }

    private void validateRequestLine(String requestLine) {
        if (requestLine == null) {
            throw new IllegalArgumentException("요청이 비어 있습니다");
        }
        if (requestLine.split(REQUEST_LINE_DELIMITER).length < REQUEST_LINE_MIN_LENGTH) {
            throw new IllegalArgumentException("RequestLine이 잘못된 요청입니다");
        }
    }

    public boolean isMethod(HttpMethod method) {
        return this.method.isMethod(method);
    }

    public boolean isPath(String path) {
        return this.path.equals(path);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}