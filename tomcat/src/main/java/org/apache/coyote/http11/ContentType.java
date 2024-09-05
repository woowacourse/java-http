package org.apache.coyote.http11;

public enum ContentType {
    HTML("text/html");

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
