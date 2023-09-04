package org.apache.coyote.httprequest.header;

public class SecChUaHeader implements RequestHeader {

    private final String option;

    public SecChUaHeader(final String option) {
        this.option = option;
    }

    @Override
    public String getValue() {
        return option;
    }
}
