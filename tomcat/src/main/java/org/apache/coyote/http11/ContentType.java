package org.apache.coyote.http11;

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

        if ("html".equals(extension) || "/".equals(extension)) {
            return HTML;
        }
        if ("css".equals(extension)) {
            return CSS;
        }
        if ("js".equals(extension)) {
            return JS;
        }

        throw new IllegalArgumentException("허용되지 않는 타입입니다.");
    }

    public String toHeader() {
        return "Content-Type: " + type + ";charset=utf-8 ";
    }
}
