package org.apache.coyote.http11;

public enum ContentType {
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    ALL("*/*");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean in(String value) {
        return value.contains(this.value);
    }
}
