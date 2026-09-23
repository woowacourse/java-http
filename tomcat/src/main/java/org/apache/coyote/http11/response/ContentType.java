package org.apache.coyote.http11.response;

public enum ContentType {

    HTML("text/html"),
    CSS("text/css");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    String value() {
        return value;
    }

    static ContentType fromResourceName(String resourceName) {
        return resourceName.endsWith(".css") ? CSS : HTML;
    }
}
