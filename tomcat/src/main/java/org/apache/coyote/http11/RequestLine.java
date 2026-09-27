package org.apache.coyote.http11;

import java.io.IOException;

public class RequestLine {

    private final String method;
    private final String requestTarget;
    private final String httpVersion;

    public RequestLine(String method, String requestTarget, String httpVersion) {
        this.method = method;
        this.requestTarget = requestTarget;
        this.httpVersion = httpVersion;
    }

    public static RequestLine parse(String line) throws IOException {
        if (line == null) {
            throw new IOException("요청 줄이 없습니다.");
        }

        String[] parts = line.trim().split("\\s+");

        if (parts.length != 3) {
            throw new IOException("잘못된 요청 줄: " + line);
        }
        if (!parts[1].startsWith("/")) {
            throw new IOException("잘못된 요청 경로: " + parts[1]);
        }
        if (!parts[2].equals("HTTP/1.1")) {
            throw new IOException("지원하지 않는 HTTP 버전: " + parts[2]);
        }

        return new RequestLine(parts[0], parts[1], parts[2]);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        int questionMark = requestTarget.indexOf('?');

        if (questionMark == -1) {
            return requestTarget;
        }
        return requestTarget.substring(0, questionMark);
    }

    public String getQueryString() {
        int questionMark = requestTarget.indexOf('?');

        if (questionMark == -1) {
            return null;
        }
        return requestTarget.substring(questionMark + 1);
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
