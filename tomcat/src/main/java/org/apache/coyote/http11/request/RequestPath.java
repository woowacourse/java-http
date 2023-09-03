package org.apache.coyote.http11.request;

public class RequestPath {
    private final String path;

    public RequestPath(final String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
