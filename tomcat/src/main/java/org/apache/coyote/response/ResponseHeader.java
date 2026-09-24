package org.apache.coyote.response;

public enum ResponseHeader {

    ACCEPT_RANGES("Accept-Ranges"),
    AGE("Age"),
    E_TAG("ETag"),
    LOCATION("Location"),
    PROXY_AUTHENTICATE("Proxy-Authenticate"),
    RETRY_AFTER("Retry-After"),
    SERVER("Server"),
    VARY("Vary"),
    WWW_AUTHENTICATE("WWW-Authenticate"),
    ;

    private final String fieldName;

    ResponseHeader(String fieldName) {
        this.fieldName = fieldName;
    }
}
