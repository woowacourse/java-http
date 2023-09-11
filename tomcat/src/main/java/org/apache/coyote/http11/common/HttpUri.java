package org.apache.coyote.http11.common;

public enum HttpUri {
    INDEX_HTML("/index.html"),
    UNAUTHORIZED_HTML("/401.html"),
    REGISTER("/register.html"),
    LOGIN_HTML("/login.html"),
    ;

    private final String uri;

    HttpUri(final String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
