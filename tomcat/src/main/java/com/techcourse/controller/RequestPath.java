package com.techcourse.controller;

public enum RequestPath {

    ROOT("/"),
    INDEX("/index.html"),
    LOGIN("/login.html"),
    REGISTER("/register.html"),
    UNAUTHORIZED("/401.html"),
    NOT_FOUND("/404.html"),
    METHOD_NOT_ALLOWED("/405.html"),
    INTERNAL_SERVER_ERROR("/500.html"),
    ;

    private final String path;

    RequestPath(final String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
