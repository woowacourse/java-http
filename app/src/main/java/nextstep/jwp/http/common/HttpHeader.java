package nextstep.jwp.http.common;

public enum HttpHeader {

    COOKIE("Cookie"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    TRANSFER_ENCODING("Transfer-Encoding");

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public String toLowerString() {
        return value.toLowerCase();
    }

    public String toRawString() {
        return value;
    }
}
