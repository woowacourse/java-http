package org.apache.catalina.webutils;

public enum FileType {
    HTML("html");

    private final String value;

    FileType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
