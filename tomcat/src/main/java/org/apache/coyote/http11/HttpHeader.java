package org.apache.coyote.http11;

public enum HttpHeader {

    ACCEPT("Accept"),
    ACCEPT_ENCODING("Accept-Encoding"),
    CONNECTION("Connection"),
    HOST("Host"),
    USER_AGENT("User-Agent"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie");

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public boolean isSameValue(String value) {
        return this.value.equals(value);
    }
}
