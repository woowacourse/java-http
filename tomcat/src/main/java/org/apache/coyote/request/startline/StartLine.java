package org.apache.coyote.request.startline;

import org.apache.coyote.request.HttpRequestPath;

public class StartLine {

    private static final int START_LINE_ELEMENT_SIZE = 3;
    private static final String ROOT = "/";
    private static final int URI_INDEX = 1;
    private static final int METHOD_INDEX = 0;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String START_LINE_DELIMITER = " ";

    private final HttpMethod method;
    private final HttpRequestPath requestPath;
    private final HttpVersion httpVersion;

    private StartLine(HttpMethod method, String requestUri, String httpVersion) {
        this.method = method;
        this.requestPath = HttpRequestPath.from(requestUri);
        this.httpVersion = HttpVersion.from(httpVersion);
    }

    public static StartLine from(String startLine) {
        final String[] startLineElements = splitStartLine(startLine);

        final HttpMethod httpMethod = HttpMethod.from(startLineElements[METHOD_INDEX]);
        final String requestUri = startLineElements[URI_INDEX];
        final String httpVersion = startLineElements[HTTP_VERSION_INDEX];

        return new StartLine(httpMethod, requestUri, httpVersion);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestPath() {
        return requestPath.getUri();
    }

    private static String[] splitStartLine(String startLine) {
        final String[] startLineElements = startLine.split(START_LINE_DELIMITER);

        checkStartLineSize(startLineElements);
        checkRequestUri(startLineElements[URI_INDEX]);

        return startLineElements;
    }

    private static void checkStartLineSize(String[] startLineElements) {
        if (startLineElements.length != START_LINE_ELEMENT_SIZE) {
            throw new IllegalArgumentException("잘못된 HTTP 요청입니다.");
        }
    }

    private static void checkRequestUri(String requestUri) {
        if (!requestUri.startsWith(ROOT)) {
            throw new IllegalArgumentException("잘못된 자원 요청입니다.");
        }
    }
}
