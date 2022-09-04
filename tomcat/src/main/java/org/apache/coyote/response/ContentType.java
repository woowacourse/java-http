package org.apache.coyote.response;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("application/javascript"),
    DEFAULT("text/html");

    private static final String EXTENSION_DELIMITER = "\\.";
    private static final int EXTENSION_INDEX = 1;
    private static final int NO_EXTENSION_SIZE = 1;

    private final String contentType;

    ContentType(final String contentType) {
        this.contentType = contentType;
    }

    public static ContentType from(final String requestUrl) {
        String[] splitRequestUrl = requestUrl.split(EXTENSION_DELIMITER);
        if (splitRequestUrl.length == NO_EXTENSION_SIZE) {
            return DEFAULT;
        }

        String extension = splitRequestUrl[EXTENSION_INDEX];
        return Arrays.stream(ContentType.values())
                .filter(contentType -> isSameExtension(contentType, extension))
                .findAny()
                .orElse(DEFAULT);
    }

    private static boolean isSameExtension(ContentType contentType, String extension) {
        String contentTypeName = contentType.name().toLowerCase();

        return contentTypeName.contains(extension);
    }

    @Override
    public String toString() {
        return this.contentType;
    }
}
