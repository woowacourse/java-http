package org.apache.coyote.http11;

public enum ContentType {
    TEXT_HTML("text/html");

    private String value;

    ContentType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
