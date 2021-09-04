package nextstep.jwp.constants;

public enum HeaderType {
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    CONTENT_TYPE("Content-Type"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie");

    private final String value;

    HeaderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
