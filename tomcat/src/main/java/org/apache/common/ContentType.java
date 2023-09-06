package org.apache.common;

import java.util.Arrays;

public enum ContentType {

    TEXT_CSS("text/css", ".css"),
    TEXT_HTML("text/html", ".html"),
    TEXT_PLAIN("text/plain", "text"),
    APPLICATION_JS("application/js", ".js"),
    APPLICATION_JSON("application/json", "json"),
    PERMIT_ALL("*/*", "*");

    private final String value;
    private final String extension;

    ContentType(String value, String extension) {
        this.value = value;
        this.extension = extension;
    }

    public static ContentType from(String value) {
        return Arrays.stream(values())
                .filter(contentType -> value.endsWith(contentType.extension))
                .findAny()
                .orElse(ContentType.PERMIT_ALL);
    }

    public String getValue() {
        return value;
    }
}
