package org.apache.coyote.http11;

public enum Type {
    HTML("text/html"),
    CSS("text/css"),
    JAVASCRIPT("text/javascript");

    private final String value;

    Type(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
