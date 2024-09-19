package org.apache.coyote.http11.message.header;

public enum HttpHeaderFieldType {
    CONNECTION("Connection", true, true),
    CONTENT_TYPE("Content-Type", true, true),
    CONTENT_LENGTH("Content-Length", true, true),

    HOST("Host", true, false),
    ACCEPT("Accept", true, false),
    COOKIE("Cookie", true, false),

    LOCATION("Location", false, true),
    SET_COOKIE("Set-Cookie", false, true);

    private final String value;
    private final boolean isRequestHeaderAllowed;
    private final boolean isResponseHeaderAllowed;

    HttpHeaderFieldType(
            final String value,
            final boolean isRequestHeaderAllowed,
            final boolean isResponseHeaderAllowed
    ) {
        this.value = value;
        this.isRequestHeaderAllowed = isRequestHeaderAllowed;
        this.isResponseHeaderAllowed = isResponseHeaderAllowed;
    }

    public String getValue() {
        return value;
    }
}
