package org.apache.coyote.http11;

public enum ContentType {
    HTML("html"),
    CSS("css"),
    ;

    private final String name;

    ContentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
