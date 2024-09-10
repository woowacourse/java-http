package org.apache.coyote.http11;

public enum ContentType {

    UTF_8(";charset=utf-8")
    ;

    private final String charset;

    ContentType(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return charset;
    }
}
