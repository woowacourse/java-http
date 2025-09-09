package org.apache.coyote.http11;

public enum ContentType {
    NONE(""),
    HTML("text/html"),
    CSS("text/css"),
    JAVASCRIPT("text/javascript")
    ;

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean isText() {
        return type.contains("text");
    }
}
