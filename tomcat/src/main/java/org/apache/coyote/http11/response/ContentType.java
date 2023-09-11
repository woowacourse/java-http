package org.apache.coyote.http11.response;

import javassist.NotFoundException;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html"),
    CSS("text/css"),
    JS("application/x-javascript");

    public static final String SEPARATOR = "\\.";
    public static final int EXTENSION_INDEX = 1;
    private final String value;

    ContentType(final String value) {
        this.value = value + ";charset=utf-8";
    }

    public static ContentType findBy(final String content) throws NotFoundException {
        final String[] fileInfo = content.split(SEPARATOR);

        if (fileInfo.length > EXTENSION_INDEX) {
            final String extension = fileInfo[EXTENSION_INDEX].toUpperCase();

            return Arrays.stream(values())
                         .filter(type -> type.name().equals(extension))
                         .findFirst()
                         .orElse(HTML);
        }

        throw new NotFoundException("찾을 수 없습니다.");
    }

    public String getValue() {
        return value;
    }
}
