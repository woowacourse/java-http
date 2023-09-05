package org.apache.coyote.http11.response;

public enum Resource {
    REGISTER("register.html"),
    LOGIN("login.html"),
    INDEX("index.html"),
    UNAUTHORIZED("401.html"),
    ROOT("/");

    private final String path;

    Resource(final String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
