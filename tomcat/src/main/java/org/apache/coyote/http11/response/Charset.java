package org.apache.coyote.http11.response;

public enum Charset {
    UTF_8("charset=utf-8");
    private final String charSetType;

    Charset(final String charSetType) {
        this.charSetType = charSetType;
    }

    public String getType() {
        return charSetType;
    }
}
