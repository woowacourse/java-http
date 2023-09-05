package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JS("text/js", "js");

    private static final String EXTENSION_DELIMITER = "\\.";

    private final String type;
    private final String extension;

    ContentType(final String type, final String extension) {
        this.type = type;
        this.extension = extension;
    }

    public static ContentType from(final String filePath) {
        final String[] split = filePath.split(EXTENSION_DELIMITER);
        final String extension = split[split.length - 1];

        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.extension.equals(extension))
                .findAny()
                .orElse(HTML);
    }

    public String toHeader() {
        return "Content-Type: " + type + ";charset=utf-8 ";
    }

    public String getExtension() {
        return extension;
    }
}
