package org.apache.coyote.common;

public enum Charset {

    UTF8("utf-8");

    private final String value;

    Charset(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
