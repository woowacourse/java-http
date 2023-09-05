package org.apache.coyote.common;

public enum CharacterSet {

    UTF_8("utf-8");

    private static final String CHARACTER_SET = "charset";

    private final String value;

    CharacterSet(final String value) {
        this.value = value;
    }

    public String value() {
        return CHARACTER_SET + "=" + value;
    }
}
