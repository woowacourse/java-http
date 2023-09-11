package org.apache.catalina.controller;

enum StaticResourceUri {
    DEFAULT_PAGE("/index.html"),
    NOT_FOUND_PAGE("/404.html"),
    LOGIN_PAGE("/login.html"),
    REGISTER_PAGE("/register.html");

    private final String uri;

    StaticResourceUri(final String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
