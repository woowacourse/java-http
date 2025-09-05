package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Messages#http_requests">...</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc2616#section-4.1">...</a>
 */
public class RequestLine {

    private HttpMethod method;
    private String requestTarget;
    private String protocol;

    public RequestLine(String requestLine) {
        validateRequestLine(requestLine);

        String[] parsed = requestLine.trim().split(" ", 3);
        this.method = HttpMethod.from(parsed[0]);
        this.requestTarget = parsed[1];
        this.protocol = parsed[2];
    }

    private void validateRequestLine(String requestLine) {
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("request line is empty");
        }

        String[] parsed = requestLine.trim().split(" ", 3);
        if (parsed.length != 3) {
            throw new IllegalArgumentException("length of request line is not 3");
        }

        String method = parsed[0];
        String path = parsed[1];
        String version = parsed[2];

        // 1. method 검증
        if (!Arrays.stream(HttpMethod.values())
                .map(HttpMethod::getName)
                .toList()
                .contains(method)) {
            throw new IllegalArgumentException(method + ": method doesn't exist");
        }

        // 2. path 검증
        if (!(path.startsWith("/") || "*".equals(path))) {
            throw new IllegalArgumentException(path + ": invalid path");
        }

        // 3. protocol 검증 (HTTP 1.0과 1.1만 허용)
        if (!List.of("HTTP/1.0", "HTTP/1.1").contains(version)) {
            throw new IllegalArgumentException(version + ": invalid protocol");
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getProtocol() {
        return protocol;
    }

    @Override
    public String toString() {
        return String.join(" ", method.getName(), requestTarget, protocol);
    }
}
