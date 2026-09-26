package org.apache.coyote.request;

public class RequestLine {
    private final HttpMethod httpMethod;
    private final String requestTarget;
    private final String httpVersion;

    public static RequestLine parse(String line) {
        String[] requestLineParts = line.split(" ");

        if (requestLineParts.length != 3) {
            throw new IllegalArgumentException("요청라인이 올바르지 않습니다.");
        }

        String httpMethod = requestLineParts[0];
        String requestTarget = requestLineParts[1];
        String httpVersion = requestLineParts[2];

        if (requestTarget.contains("?")) {
            String[] uriParts = requestTarget.split("\\?", 2);
            requestTarget = uriParts[0];
        }

        return new RequestLine(httpMethod, requestTarget, httpVersion);
    }

    private RequestLine(String method, String requestTarget, String httpVersion) {
        validateMethod(method);
        validateRequestTarget(requestTarget);
        validateHttpVersion(httpVersion);

        this.httpMethod = HttpMethod.valueOf(method);
        this.requestTarget = requestTarget;
        this.httpVersion = httpVersion;
    }

    private void validateHttpVersion(String httpVersion) {
        if (httpVersion == null || httpVersion.isBlank() || !httpVersion.startsWith("HTTP/")) {
            throw new IllegalArgumentException("HTTP 버전이 올바르지 않습니다.");
        }

        if (!httpVersion.equals("HTTP/1.1") && !httpVersion.equals("HTTP/1.0")) {
            throw new IllegalArgumentException("지원하지 않는 HTTP 버전입니다.");
        }
    }

    private void validateRequestTarget(String requestTarget) {
        if (requestTarget == null || requestTarget.isBlank() || !requestTarget.startsWith("/")) {
            throw new IllegalArgumentException("요청 대상이 올바르지 않습니다.");
        }
    }

    private void validateMethod(String method) {
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("요청 메서드가 올바르지 않습니다.");
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestTarget() {
        return requestTarget;
    }
}
