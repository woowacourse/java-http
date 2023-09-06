package org.apache.coyote.http.response;

public enum ContentType {

    HTML("text/html;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    JS("text/javascript;charset=utf-8"),
    ICO("image/x-icon"),
    ;

    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
