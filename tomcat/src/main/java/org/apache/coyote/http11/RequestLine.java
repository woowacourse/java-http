package org.apache.coyote.http11;

import java.io.IOException;

public final class RequestLine {

    private final String method;
    private final String requestTarget;
    private final String httpVersion;

    public RequestLine(String requestLine) throws IOException {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IOException("요청 줄이 없습니다.");
        }

        String[] elements = requestLine.split(" ");
        if (elements.length != 3
                || elements[0].isBlank()
                || elements[1].isBlank()
                || elements[2].isBlank()) {
            throw new IOException("요청 줄은 메서드, 요청 대상, HTTP 버전으로 구성되어야 합니다.");
        }

        method = elements[0];
        requestTarget = elements[1];
        httpVersion = elements[2];
    }

    public String getMethod() {
        return method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getPath() {
        int querySeparatorIndex = requestTarget.indexOf('?');
        if (querySeparatorIndex < 0) {
            return requestTarget;
        }
        return requestTarget.substring(0, querySeparatorIndex);
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
