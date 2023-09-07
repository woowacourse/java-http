package org.apache.coyote.http11.enums;

public enum Path {

    INDEX_URL("static/index.html"),
    LOGIN_URL("/login"),
    LOGIN_WITH_PARAM_URL("/login?"),
    REGISTER_URL("/register"),

    LOGIN_HTML("static/login.html"),
    REGISTER_HTML("static/register.html"),

    REDIRECT_INDEX_HTML("redirect:static/index.html"),
    UNAUTHORIZED_HTML("redirect:static/401.html"),

    STATIC("static"),
    REDIRECT("redirect");

    private final String value;

    Path(final String value) {
        this.value = value;
    }

    public static boolean isContainParam(String path) {
        return path.contains("?");
    }

    public String getValue() {
        return value;
    }
}
