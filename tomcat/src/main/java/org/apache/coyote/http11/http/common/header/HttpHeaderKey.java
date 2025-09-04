package org.apache.coyote.http11.http.common.header;

public enum HttpHeaderKey {

    CONTENT_LENGTH("content-length"),
    CONTENT_TYPE("content-type"),
    ;

    private final String value;

    HttpHeaderKey(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
