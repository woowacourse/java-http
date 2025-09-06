package org.apache.coyote.http11.domain;

public enum ContentType {
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "application/javascript;charset=utf-8"),
    PNG(".png", "image/png"),
    ICO(".ico", "image/x-icon"),
    ;

    private static final ContentType DEFAULT = HTML;
    private final String extension;
    private final String value;

    ContentType(String extension, String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType fromPath(String resourcePath) {
        final int lastDotIndex = resourcePath.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return DEFAULT;
        }

        final String extension = resourcePath.substring(lastDotIndex).toLowerCase();

        for (ContentType type : values()) {
            if (type.extension.equals(extension)) {
                return type;
            }
        }

        return DEFAULT;
    }

    public String getValue() {
        return value;
    }

}

