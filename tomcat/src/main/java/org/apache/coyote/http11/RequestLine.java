package org.apache.coyote.http11;

public class RequestLine {

    private final String method;
    private final String uri;
    private final String httpVersion;

    public RequestLine(String line) {
        String[] parts = line.split(" ");
        validateParts(parts);

        this.method = parts[0];
        this.uri = parts[1];
        this.httpVersion = parts[2];
    }

    private void validateParts(String[] parts) {
        if (parts.length != 3
                || parts[0].isBlank()
                || parts[1].isBlank()
                || parts[2].isBlank()) {
            throw new IllegalArgumentException("잘못된 HTTP 요청 라인입니다.");
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        int index = uri.indexOf('?');
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    public String getQueryString() {
        int index = uri.indexOf('?');
        if (index == -1) {
            return "";
        }
        return uri.substring(index + 1);
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
