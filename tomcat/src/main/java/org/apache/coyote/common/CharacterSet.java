package org.apache.coyote.common;

public enum CharacterSet {

    UTF_8("utf-8");

    private static final String CHARACTER_SET = "charset";

    private final String source;

    CharacterSet(final String source) {
        this.source = source;
    }

    public String source() {
        return CHARACTER_SET + "=" + source;
    }
}
