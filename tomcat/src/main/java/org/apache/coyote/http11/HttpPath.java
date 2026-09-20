package org.apache.coyote.http11;

public class HttpPath {
    // Http Path는 대소문자 구분을 한다.
    private final String path;

    public HttpPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
