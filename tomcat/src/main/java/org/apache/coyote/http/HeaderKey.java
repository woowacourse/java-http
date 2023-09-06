package org.apache.coyote.http;

public enum HeaderKey {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    ;

    public final String value;

    HeaderKey(String value) {
        this.value = value;
    }
}
