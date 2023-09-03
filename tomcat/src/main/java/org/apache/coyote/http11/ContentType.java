package org.apache.coyote.http11;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    ICO("image/ico");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
