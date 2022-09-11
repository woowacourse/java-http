package nextstep.jwp.vo;

public enum HeaderKey {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CHARSET_UTF_8(";charset=utf-8"),
    COOKIE("Cookie"),
    JSESSION_ID("JSESSIONID"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location");

    private final String name;

    HeaderKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
