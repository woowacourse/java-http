package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String path;
    private final String queryString;
    private final String httpVersion;

    public RequestLine(String requestLine) {
        validateRequestLine(requestLine);
        String[] parts = parseRequestLine(requestLine);
        validateLine(parts);
        this.method = parts[0];
        String[] requestTargets = parseRequestTarget(parts[1]);
        validatePath(requestTargets);
        this.path = requestTargets[0];
        this.queryString = requestTargets[1];
        this.httpVersion = parts[2];
    }

    private void validateRequestLine(String requestLine) {
        if (requestLine == null) {
            throw new IllegalArgumentException("요청 줄은 null일 수 없습니다");
        }
    }

    private void validateLine(String[] parts) {
        if (parts.length != 3) {
            throw new IllegalArgumentException("요청 줄은 'METHOD REQUEST_TARGET HTTP_VERSION' 형식이어야 합니다.");
        }

        for (String part : parts) {
            if (part.isBlank()) {
                throw new IllegalArgumentException("요청 대상은 비어 있을 수 없습니다.");
            }
        }

        if (!parts[2].equals("HTTP/1.1")) {
            throw new IllegalArgumentException("지원하지 않는 HTTP 버전입니다.");
        }
    }

    private void validatePath(String[] targets) {
        if (targets[0].isBlank()) {
            throw new IllegalArgumentException("요청 경로는 비어 있을 수 없습니다.");
        }
    }

    private String[] parseRequestLine(String requestLine) {
        return requestLine.split(" ", 3);
    }

    private String[] parseRequestTarget(String requestTarget) {
        int queryIndex = requestTarget.indexOf("?");
        String[] targets = {requestTarget, ""};

        if (queryIndex != -1) {
            targets[0] = requestTarget.substring(0, queryIndex);
            targets[1] = requestTarget.substring(queryIndex + 1);
            return targets;
        }

        return targets;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
