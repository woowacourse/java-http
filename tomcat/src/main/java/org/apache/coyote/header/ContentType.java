package org.apache.coyote.header;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css");

    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    public static ContentType of(final String value) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.value.equals(value))
                .findFirst()
                .orElse(HTML);
    }


    @Override
    public String toString() {
        return this.value;
    }
}
