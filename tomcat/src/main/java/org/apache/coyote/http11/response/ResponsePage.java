package org.apache.coyote.http11.response;

public enum ResponsePage {
    ROOT_PAGE("/"),
    INDEX_PAGE("/index.html"),
    LOGIN_PAGE("/login.html"),
    REGISTER_PAGE("/register.html"),
    UNAUTHORIZED_PAGE("/401.html"),
    NOT_FOUND_PAGE("/404.html"),
    INTERNAL_SERVER_ERROR_PAGE("/500.html");

    private final String resource;

    ResponsePage(final String resource) {
        this.resource = resource;
    }

    public String gerResource() {
        return resource;
    }
}
