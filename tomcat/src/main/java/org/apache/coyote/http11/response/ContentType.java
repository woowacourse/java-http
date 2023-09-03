package org.apache.coyote.http11.response;

public enum ContentType {
    HTML("text/html;charset=utf-8");

    private final String type;

    ContentType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
