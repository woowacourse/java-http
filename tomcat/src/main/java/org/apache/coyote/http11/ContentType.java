package org.apache.coyote.http11;

public enum ContentType {

    CSS("text/css"),
    HTML("text/html"),
    ALL("*/*"),
    URL_ENCODED("application/x-www-form-urlencoded"),
    ;

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
