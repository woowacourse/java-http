package com.techcourse.controller;

public enum PagePath {

    INDEX_PAGE("/index.html"),
    REGISTER_PAGE("/register.html"),
    LOGIN_PAGE("/login.html"),
    UNAUTHORIZED_PAGE("/401.html"),
    NOT_FOUND_PAGE("/404.html")
    ;

    private final String path;

    PagePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
