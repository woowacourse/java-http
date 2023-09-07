package org.apache.coyote.http11.servlet;

public enum Page {
    INDEX("/index.html"),
    LOGIN("/login.html"),
    REGISTER("/register.html"),
    BAD_REQUEST("/400.html"),
    UNAUTHORIZED("/401.html"),
    NOT_FOUND("/404.html")
    ;

    private String uri;

    Page(final String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
