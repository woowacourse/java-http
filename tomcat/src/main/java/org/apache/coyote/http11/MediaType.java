package org.apache.coyote.http11;

public enum MediaType {

    TEXT_HTML("text/html;charset=utf-8"),
    TEXT_CSS("text/css;charset=utf-8"),
    APPLICATION_JAVASCRIPT("application/javascript;charset=utf-8"),
    ;

    private String value;

    MediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
