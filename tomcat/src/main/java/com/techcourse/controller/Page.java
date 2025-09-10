package com.techcourse.controller;

public enum Page {

    INDEX("/index.html"),
    LOGIN("/login.html"),
    REGISTER("/register.html"),
    BAD_REQUEST("/400.html"),
    UNAUTHORIZED("/401.html"),
    NOT_FOUND("/404.html"),
    METHOD_NOT_ALLOWED("/405.html"),
    INTERNAL_SERVER_ERROR("/500.html")
    ;

    private final String path;

    Page(final String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
