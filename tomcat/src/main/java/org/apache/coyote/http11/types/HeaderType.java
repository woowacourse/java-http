package org.apache.coyote.http11.types;

public enum HeaderType {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    ;

    private final String type;

    HeaderType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
