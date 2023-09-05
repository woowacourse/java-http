package org.apache.coyote.http11;

public enum Url {

    INDEX("static/index.html"),
    LOGIN("/login"),
    LOGIN_WITH_PARAM("/login?"),
    REGISTER("/register"),

    LOGIN_HTML("static/login.html"),
    UNAUTHORIZED("static/401.html"),
    REGISTER_HTML("static/register.html"),

    STATIC("static");

    private final String url;

    Url(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public static boolean isContainParam(String path) {
        return path.contains("?");
    }
}
