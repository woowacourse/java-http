package org.apache.coyote.constant;

public enum MediaType {

    TEXT_HTML("text/html"),
    ;

    private final String value;

    MediaType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
