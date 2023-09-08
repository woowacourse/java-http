package nextstep.jwp.controller;

public enum Path {

    HOME("/"),
    MAIN("/index.html"),
    LOGIN("/login.html"),
    REGISTER("/register.html"),
    UNAUTHORIZED("/401.html"),
    NOT_FOUND("/404.html");

    private final String value;

    Path(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
