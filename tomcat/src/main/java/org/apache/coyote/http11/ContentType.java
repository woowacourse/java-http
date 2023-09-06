package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html"),
    CSS("text/css"),
    ALL("*/*");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public static ContentType from(String contentType) {
        return Arrays.stream(values())
                .filter(resource -> resource.getValue().equals(contentType))
                .findFirst()
                .orElse(HTML);
    }

    public String getValue() {
        return value;
    }
}
