package org.apache.coyote.httprequest.header;

public class ContentLengthHeader implements RequestHeader {

    private final String length;

    public ContentLengthHeader(final String length) {
        this.length = length;
    }

    @Override
    public String getValue() {
        return length;
    }
}
