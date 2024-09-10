package org.apache.http.header;

public enum StandardHttpHeader {
    ACCEPT("Accept"),
    ACCEPT_RANGES("Accept-Ranges"),
    AUTHORIZATION("Authorization"),
    CONNECTION("Connection"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    HOST("Host"),
    LOCATION("Location"),
    ORIGIN("Origin"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String value;

    StandardHttpHeader(String value) {
        this.value = value;
    }

    public boolean equalsIgnoreCase(String value) {
        return this.value.equalsIgnoreCase(value);
    }

    public String getValue() {
        return value;
    }
}
