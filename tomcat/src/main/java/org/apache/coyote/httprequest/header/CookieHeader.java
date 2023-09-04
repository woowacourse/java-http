package org.apache.coyote.httprequest.header;

public class CookieHeader implements RequestHeader {

    private final String value;

    public CookieHeader(final String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
