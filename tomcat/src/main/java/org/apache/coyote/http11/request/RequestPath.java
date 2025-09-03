package org.apache.coyote.http11.request;

public class RequestPath {
    private final String path;

    public RequestPath(String path) {
        this.path = path;
    }

    public static RequestPath of(String path) {
        return new RequestPath(path);
    }

    public String getPath() {
        return path;
    }
}
