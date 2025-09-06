package org.apache.coyote.http11;

public class Header {
    private final String header;
    private final String value;

    public Header(final String header, final String value) {
        this.header = header;
        this.value = value;
    }
}
