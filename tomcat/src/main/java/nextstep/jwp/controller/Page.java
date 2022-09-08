package nextstep.jwp.controller;

import org.apache.coyote.http11.response.Resource;

public enum Page {

    INDEX("index.html"),
    LOGIN("login.html"),
    REGISTER("register.html"),
    UNAUTHORIZED("401.html"),
    BAD_REQUEST("404.html"),
    METHOD_NOT_ALLOWED("405.html"),
    ;

    private final Resource resource;

    Page(final String fileName) {
        this.resource = new Resource(fileName);
    }

    public Resource getResource() {
        return resource;
    }
}
