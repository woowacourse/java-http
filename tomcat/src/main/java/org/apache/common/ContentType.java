package org.apache.common;

import java.util.Arrays;
import java.util.Objects;

public enum ContentType {

    TEXT_CSS("text/css"),
    TEXT_HTML("text/html"),
    APPLICATION_JS("application/js"),
    PERMIT_ALL("*/*");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public static ContentType of(String value) {
        return Arrays.stream(values())
                .filter(contentType -> Objects.equals(contentType.getValue(), value))
                .findAny()
                .orElse(ContentType.PERMIT_ALL);
    }

    public String getValue() {
        return value;
    }
}
