package org.apache.coyote.http11.response;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/js");

    private static final String EXTENSION_DELIMITER = "\\.";

    private final String type;

    ContentType(final String type) {
        this.type = type;
    }

    public static ContentType from(final String filePath) {
        final String[] split = filePath.split(EXTENSION_DELIMITER);
        final String extension = split[split.length - 1];

        if ("css".equals(extension)) {
            return CSS;
        }
        if ("js".equals(extension)) {
            return JS;
        }

        return HTML;
    }

    public String toHeader() {
        return "Content-Type: " + type + ";charset=utf-8 ";
    }

    public String getExtension() {
        final String[] split = type.split("/");
        return "." + split[split.length - 1];
    }
}
