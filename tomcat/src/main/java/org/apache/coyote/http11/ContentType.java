package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html"),
    CSS("css", "text/css")
    ;

    private final String extension;
    private final String value;

    ContentType(final String extension, final String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType of(final String extension) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.extension.equals(extension))
                .findFirst()
                .orElse(HTML);
    }

    public String getValue() {
        return value;
    }
}
