package org.apache.coyote.http11;

public final class RequestLine {

    private final String method;
    private final String path;
    private final String queryString;
    private final String version;

    public RequestLine(final String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("요청 첫 줄이 비어 있습니다.");
        }

        final String[] parts = line.trim().split("\\s+");
        if (parts.length != 3 || !parts[1].startsWith("/") || !parts[2].matches("HTTP/\\d+\\.\\d+")) {
            throw new IllegalArgumentException("잘못된 요청 첫 줄: " + line);
        }

        method = parts[0];
        version = parts[2];

        final int questionMark = parts[1].indexOf('?');
        path = questionMark < 0 ? parts[1] : parts[1].substring(0, questionMark);
        queryString = questionMark < 0 ? "" : parts[1].substring(questionMark + 1);
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

    public String getVersion() {
        return version;
    }
}
