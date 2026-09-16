package org.apache.coyote.http11;

public enum ContextType {
    HTML("html"),
    CSS("css"),
    ;

    private final String type;

    ContextType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
