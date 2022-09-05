package org.apache.coyote.http11.response.header;

public enum ContentType {

    TEXT_JS("text/js;charset=utf-8 "),
    TEXT_CSS("text/css;charset=utf-8 "),
    TEXT_HTML("text/html;charset=utf-8 "),
    NONE(""),
    ;

    private static final String CONTENT_TYPE_HEADER_KEY = "Content-Type: ";

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return CONTENT_TYPE_HEADER_KEY + value;
    }
}
