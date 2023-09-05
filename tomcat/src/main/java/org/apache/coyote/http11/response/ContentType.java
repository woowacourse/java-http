package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    SVG("image/svg+xml");

    private static final int EXTENSION_INDEX = 1;
    private static final String EXTENSION_DELIMITER = "\\.";
    private final String type;

    ContentType(final String type) {
        this.type = type;
    }

    public static ContentType findByName(String fileName) {
        final String extension = fileName.split(EXTENSION_DELIMITER)[EXTENSION_INDEX];
        return Arrays.stream(values())
                .filter(contentType -> contentType.name().equalsIgnoreCase(extension))
                .findAny()
                .orElse(HTML);
    }

    public String getType() {
        return type;
    }
}
