package org.apache.coyote.http11.domain;

public class RequestPath {

    private final String path;

    public RequestPath(String path) {
        if (path == null) {
            throw new NullPointerException("경로가 존재하지 않습니다.");
        }
        this.path = path.trim();
    }

    public String getPath() {
        return path;
    }
}
