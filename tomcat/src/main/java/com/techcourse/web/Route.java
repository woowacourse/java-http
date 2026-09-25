package com.techcourse.web;

public enum Route {

    LOGIN("/login"),
    REGISTER("/register");

    private final String path;

    Route(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
