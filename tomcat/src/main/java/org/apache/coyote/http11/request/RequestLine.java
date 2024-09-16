package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpVersion;

public class RequestLine {

    private static final String DELIMITER_SPACE = " ";
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private static final int REQUEST_LINE_SIZE = 3;

    private final HttpMethod httpMethod;
    private final String requestURL;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod httpMethod, String requestURL, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestURL = requestURL;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        String[] line = parseRequestLine(requestLine);

        HttpMethod httpMethod = HttpMethod.from(line[METHOD_INDEX]);
        String requestURL = line[PATH_INDEX];
        HttpVersion httpVersion = HttpVersion.from(line[VERSION_INDEX]);

        return new RequestLine(httpMethod, requestURL, httpVersion);
    }

    private static String[] parseRequestLine(String requestLine) {
        if (requestLine == null) {
            throw new IllegalArgumentException("빈 요청 라인은 처리할 수 없습니다.");
        }

        String[] line = requestLine.split(DELIMITER_SPACE);

        if (line.length != REQUEST_LINE_SIZE) {
            throw new IllegalArgumentException("RequestLine의 길이가 올바르지 않습니다.");
        }
        return line;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
