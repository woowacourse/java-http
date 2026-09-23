package org.apache.coyote.http11;

public final class RequestLine {

    private final String method;
    private final String requestTarget;
    private final String httpVersion;

    private RequestLine(String method, String requestTarget, String httpVersion) {
        this.method = method;
        this.requestTarget = requestTarget;
        this.httpVersion = httpVersion;
    }

    public static RequestLine parse(String line) {
        String[] parts = line.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("요청 줄에는 메서드, 요청 대상, 버전이 필요합니다.");
        }
        return new RequestLine(parts[0], parts[1], parts[2]);
    }

    public String method() {
        return method;
    }

    public String requestTarget() {
        return requestTarget;
    }

    public String path() {
        return requestTarget.split("\\?", 2)[0];
    }

    public String httpVersion() {
        return httpVersion;
    }
}
