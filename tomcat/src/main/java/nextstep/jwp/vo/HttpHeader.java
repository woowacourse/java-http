package nextstep.jwp.vo;

public enum HttpHeader {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CHARSET_UTF_8(";charset=utf-8"),
    COOKIE("Cookie"),
    JSESSION_ID("JSESSIONID"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location");

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
