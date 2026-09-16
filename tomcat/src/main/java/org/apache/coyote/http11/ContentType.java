package org.apache.coyote.http11;

public enum ContentType {
    HTML("html"),
    CSS("css"),
    ;

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
