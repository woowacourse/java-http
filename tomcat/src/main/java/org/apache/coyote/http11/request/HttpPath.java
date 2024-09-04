package org.apache.coyote.http11.request;

public class HttpPath {

    private final String path;

    public HttpPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
