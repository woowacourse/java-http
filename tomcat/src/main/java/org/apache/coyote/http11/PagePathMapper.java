package org.apache.coyote.http11;

public enum PagePathMapper {

    INDEX_PAGE("index.html"),
    LOGIN_PAGE("login.html"),
    REGISTER_PAGE("register.html"),
    UNAUTHORIZED_PAGE("401.html");

    private final String path;

    PagePathMapper(String path) {
        this.path = path;
    }

    public String path() {
        return this.path;
    }
}
