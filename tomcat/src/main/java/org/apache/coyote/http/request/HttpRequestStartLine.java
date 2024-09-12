package org.apache.coyote.http.request;

import org.apache.coyote.http.HttpMethod;

public class HttpRequestStartLine {

    private static final String START_LINE_DELIMITER = " ";
    private static final int EXPECTED_PARTS_COUNT = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;

    private final HttpMethod httpMethod;
    private final String requestURI;

    public HttpRequestStartLine(final String line) {
        final String[] startLine = line.split(START_LINE_DELIMITER);
        if (startLine.length != EXPECTED_PARTS_COUNT) {
            throw new IllegalArgumentException("HttpRequest의 startLine 형식이 잘못되었습니다.");
        }
        String httpMethodName = startLine[METHOD_INDEX];
        this.httpMethod = HttpMethod.findByName(httpMethodName);
        this.requestURI = startLine[URI_INDEX];
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }
}
