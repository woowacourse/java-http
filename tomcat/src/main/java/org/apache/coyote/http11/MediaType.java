package org.apache.coyote.http11;

public enum MediaType {

    CSS("text/css"),
    HTML("text/html"),
    ALL("*/*"),
    ;

    private final String type;

    MediaType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
