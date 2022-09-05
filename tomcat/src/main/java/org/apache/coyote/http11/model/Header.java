package org.apache.coyote.http11.model;

public class Header {

    private final String key;
    private final String value;

    public Header(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String getString() {
        return key + ": " + value + " ";
    }
}
