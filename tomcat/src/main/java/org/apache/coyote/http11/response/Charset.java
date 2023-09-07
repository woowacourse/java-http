package org.apache.coyote.http11.response;

public enum Charset {
    UTF_8("utf-8"),
    ;

    private static final String FIELD = "charset=";

    private final String charset;

    Charset(String charset) {
        this.charset = charset;
    }

    public String charsetWithFiled() {
        return FIELD + charset();
    }

    public String charset() {
        return charset;
    }
}
