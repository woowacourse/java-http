package org.apache.coyote.http11;

public enum ContentType {
    
    HTML("text/html"),
    TEXT("text/plain"),
    CSS("text/css"),
    JAVASCRIPT("application/javascript"),
    JSON("application/json");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String get() {
        return value;
    }
}
