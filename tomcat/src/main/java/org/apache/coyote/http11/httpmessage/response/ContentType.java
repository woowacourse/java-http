package org.apache.coyote.http11.httpmessage.response;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/javascript");

    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
