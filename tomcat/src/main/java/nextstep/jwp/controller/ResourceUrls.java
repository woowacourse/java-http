package nextstep.jwp.controller;

public enum ResourceUrls {

    INDEX_HTML("/index.html"),
    UNAUTHORIZED_HTML("/401.html"),
    REGISTER_HTML("/register.html"),
    LOGIN_HTML("/login.html");

    private final String value;

    ResourceUrls(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
