package org.apache.coyote.http11.common;

public enum Charset {

    UTF_8("utf-8"),
    ;

    private static final String SUFFIX = "charset=";

    private final String value;

    Charset(String value) {
        this.value = value;
    }

    public String format() {
        return SUFFIX + value;
    }
}
