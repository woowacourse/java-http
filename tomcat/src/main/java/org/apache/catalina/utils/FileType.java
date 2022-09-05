package org.apache.catalina.utils;

public enum FileType {
    HTML("html");

    private String value;

    FileType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
