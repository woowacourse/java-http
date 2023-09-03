package org.apache.coyote.http11.header;

public enum EntityHeader implements Header {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    ;

    final String value;

    EntityHeader(final String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
