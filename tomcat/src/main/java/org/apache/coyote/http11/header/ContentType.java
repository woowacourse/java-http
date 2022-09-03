package org.apache.coyote.http11.header;

public enum ContentType {

    HTML("text/html"),
    UTF_8("charset=utf-8");

    private String value;

    ContentType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
