package nextstep.jwp.controller;

import org.apache.coyote.http11.response.Resource;

public enum Page {

    INDEX("/index.html"),
    LOGIN("/login.html"),
    REGISTER("/register.html"),
    BAD_REQUEST("/400.html"),
    UNAUTHORIZED("/401.html"),
    NOT_FOUND("/404.html"),
    METHOD_NOT_ALLOWED("/405.html"),
    ;

    private final String filePath;

    Page(final String filePath) {
        this.filePath = filePath;
    }

    public String getPath() {
        return filePath;
    }

    public Resource getResource() {
        return new Resource(filePath);
    }
}
