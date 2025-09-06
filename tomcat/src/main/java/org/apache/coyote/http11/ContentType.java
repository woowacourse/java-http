package org.apache.coyote.http11;

public enum ContentType {

    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JAVASCRIPT("application/javascript");

    private String value;

    ContentType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
