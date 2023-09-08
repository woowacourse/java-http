package org.apache.coyote.httprequest.header;

public class UnsupportedHeader implements RequestHeader {

    private final String value;

    public UnsupportedHeader(final String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
