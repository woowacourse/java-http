package com.techcourse.web;

public enum Page {

    INDEX("/index.html"),
    LOGIN("/login.html"),
    REGISTER("/register.html"),
    UNAUTHORIZED("/401.html");

    private final String path;

    Page(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
