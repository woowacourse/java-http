package org.apache.coyote.http11;

public enum ContentType {
    TEXT_HTML("text/html"),
    CSS("text/css"),
    ;
    private static final String DEFAULT_UTF8 = "charset=utf-8";
    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return String.format("%s;%s", type, DEFAULT_UTF8);
    }
}
