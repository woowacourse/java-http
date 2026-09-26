package org.apache.coyote.http11;

import java.net.URI;

public class RequestLine {

    private final String method;
    private final String path;
    private final String query;
    private final String version;

    public RequestLine(String line) {
        String[] parts = line.strip().split(" ", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("올바르지 않은 요청 시작 줄입니다.");
        }

        URI target = URI.create(parts[1]);
        if (!parts[1].startsWith("/") || target.getRawAuthority() != null
                || target.getRawFragment() != null) {
            throw new IllegalArgumentException("요청 경로는 /로 시작하는 경로여야 합니다.");
        }
        this.method = parts[0];
        this.path = target.getRawPath();
        this.query = target.getRawQuery();
        this.version = parts[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public String getVersion() {
        return version;
    }
}
