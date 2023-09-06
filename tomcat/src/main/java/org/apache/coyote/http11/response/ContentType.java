package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html;charset=utf-8");

    public static final String SEPARATOR = "\\.";
    public static final int EXTENSION_INDEX = 1;
    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    public static ContentType findBy(final String content) {
        final String extension = content.split(SEPARATOR)[EXTENSION_INDEX].toUpperCase();

        return Arrays.stream(values())
                     .filter(type -> type.value.equals(extension))
                     .findFirst()
                     .orElse(HTML);
    }
}
