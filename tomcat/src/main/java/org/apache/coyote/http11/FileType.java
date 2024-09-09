package org.apache.coyote.http11;

public enum FileType {
    HTML("text/html"),
    JAVASCRIPT("text/javascript"),
    CSS("text/css");

    private final String value;

    FileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
