package org.apache.coyote.http11.resource;

public enum CharSet {
    UTF_8("utf-8");

    private final String value;

    CharSet(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
