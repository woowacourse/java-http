package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    TEXT_HTML("text/html", "html"),
    TEXT_CSS("text/css", "css");

    private final String value;
    private final String extension;

    ContentType(final String value, final String extension) {
        this.value = value;
        this.extension = extension;
    }

    public static ContentType fromExtension(final String extension) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.extension.equals(extension))
                .findAny()
                .orElse(TEXT_HTML);
    }

    public String getValue() {
        return value;
    }
}
