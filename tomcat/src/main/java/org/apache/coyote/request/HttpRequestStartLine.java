package org.apache.coyote.request;

import org.apache.coyote.http11.HttpMethod;

public class HttpRequestStartLine {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_TARGET_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String HTTP_VERSION = "HTTP/1.1";

    private final HttpMethod method;
    private final HttpRequestUri target;

    private HttpRequestStartLine(HttpMethod method, HttpRequestUri requestTarget) {
        this.method = method;
        this.target = requestTarget;
    }

    public static HttpRequestStartLine from(String value) {
        validateBlank(value);
        String[] values = value.split(" ");

        validateVersion(values[HTTP_VERSION_INDEX]);

        HttpMethod method = HttpMethod.from(values[HTTP_METHOD_INDEX]);
        HttpRequestUri requestTarget = HttpRequestUri.from(values[REQUEST_TARGET_INDEX]);

        return new HttpRequestStartLine(method, requestTarget);
    }

    private static void validateBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("startLine이 없습니다.");
        }
    }

    private static void validateVersion(String version) {
        if (!HTTP_VERSION.equals(version)) {
            throw new IllegalArgumentException("잘못된 HTTP 버전입니다.");
        }
    }

    public String getEndPoint() {
        return target.getEndPoint();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpRequestUri getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "HttpRequestStartLine{" +
                "method=" + method +
                ", target=" + target +
                '}';
    }
}
