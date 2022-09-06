package org.apache.coyote.http11.response.header;

public enum ContentType implements HttpResponseHeader {

    TEXT_JS("text/js;charset=utf-8 "),
    TEXT_CSS("text/css;charset=utf-8 "),
    TEXT_HTML("text/html;charset=utf-8 "),
    ;

    private static final String HEADER_KEY = "Content-Type: ";

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String toHeaderFormat() {
        return HEADER_KEY + value;
    }
}
