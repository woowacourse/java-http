package nextstep.org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpHeader {
    ACCEPT("Accept"),
    COOKIE("Cookie"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie");



    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public static HttpHeader getHttpHeader(String value) {
        return Arrays.stream(HttpHeader.values())
                .filter(header -> header.value.equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String getValue() {
        return value;
    }
}
