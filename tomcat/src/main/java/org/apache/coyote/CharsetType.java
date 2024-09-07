package org.apache.coyote;

public enum CharsetType {
    UTF_8("utf-8"),
    NONE(""),
    ;

    private final String charset;

    CharsetType(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return charset;
    }
}

