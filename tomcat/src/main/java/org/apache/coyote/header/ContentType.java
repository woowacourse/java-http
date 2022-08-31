package org.apache.coyote.header;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css");

    private static final String CHARSET = "charset=utf-8";

    private final String value;

    ContentType(final String value) {
        this.value = String.join(";", value, CHARSET);
    }

    public static ContentType of(final String value) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.value.startsWith(value))
                .findFirst()
                .orElse(HTML);
    }


    @Override
    public String toString() {
        return this.value;
    }
}
