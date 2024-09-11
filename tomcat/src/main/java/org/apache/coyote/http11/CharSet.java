package org.apache.coyote.http11;

public enum CharSet {

    UTF_8(";charset=utf-8")
    ;

    private final String charset;

    CharSet(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return charset;
    }
}
