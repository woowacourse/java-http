package org.apache.coyote.request;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestStartLine {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestStartLine.class);
    private static final String START_LINE_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_TARGET_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod method;
    private final HttpRequestTarget target;
    private final HttpVersion httpVersion;

    private HttpRequestStartLine(HttpMethod method, HttpRequestTarget requestTarget, HttpVersion httpVersion) {
        this.method = method;
        this.target = requestTarget;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestStartLine from(String startLine) {
        validateBlank(startLine);
        String[] values = startLine.split(START_LINE_DELIMITER);
        HttpMethod method = HttpMethod.from(values[HTTP_METHOD_INDEX]);
        HttpRequestTarget requestTarget = HttpRequestTarget.from(values[REQUEST_TARGET_INDEX]);
        HttpVersion requestHttpVersion = HttpVersion.from(values[HTTP_VERSION_INDEX]);
        return new HttpRequestStartLine(method, requestTarget, requestHttpVersion);
    }

    private static void validateBlank(String value) {
        if (value == null || value.isBlank()) {
            log.debug("Start Line이 존재하지 않는 HTTP 요청");
            throw new IllegalArgumentException("유효하지 않은 HTTP 요청입니다.");
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getTargetPath() {
        return target.getPath();
    }

    public QueryParameters getQueryParameters() {
        return target.getQueryParameters();
    }

    public HttpVersion getVersion() {
        return httpVersion;
    }

    @Override
    public String toString() {
        return "method=" + method + " target=" + target + " httpVersion=" + httpVersion;
    }
}
