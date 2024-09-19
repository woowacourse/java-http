package com.techcourse.controller;

public enum PageEndpoint {
    INDEX_PAGE("/index.html"),
    REGISTER_PAGE("/register.html"),
    UNAUTHORIZED_PAGE("/401.html");

    private final String endpoint;

    PageEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
