package nextstep.jwp.controller;

public enum StaticResourcePath {

    INDEX_PAGE("/index.html"),
    UNAUTHORIZED_PAGE("/401.html"),
    NOT_FOUND_PAGE("/404.html"),
    CONFLICT_PAGE("/409.html");

    private final String value;

    StaticResourcePath(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
