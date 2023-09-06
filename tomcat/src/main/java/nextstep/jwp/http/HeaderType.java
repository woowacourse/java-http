package nextstep.jwp.http;

public enum HeaderType {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie");

    private final String value;

    HeaderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
