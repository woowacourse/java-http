package org.apache.coyote.http11.model;

public enum View {

    INDEX("/index.html"),
    LOGIN("/login.html"),
    REGISTER("/register.html"),
    BAD_REQUEST("/400.html"),
    UNAUTHORIZED("/401.html"),
    NOT_FOUND("404.html"),
    INTERNAL_SERVER_ERROR("500.html")
    ;

    private final String path;

    View(final String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
