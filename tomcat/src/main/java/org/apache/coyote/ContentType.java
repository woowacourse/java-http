package org.apache.coyote;

import java.util.Arrays;

public enum ContentType {
    CSS("text/css"),
    HTML("text/html");

    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    public static ContentType getContentType(final String input) {
        if (input == null) {
            return HTML;
        }

        return Arrays.stream(ContentType.values())
                .filter(contentType -> input.contains(contentType.value))
                .findFirst()
                .orElse(HTML);
    }

    public String getValueWithUTF8() {
        return value + ";charset=utf-8 ";
    }
}
