package org.apache.coyote.http11.header;

public enum ResponseHeader implements Header {

    LOCATION("Location"),
    ;

    final String value;

    ResponseHeader(final String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
