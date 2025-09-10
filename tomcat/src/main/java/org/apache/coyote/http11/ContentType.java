package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html;charset=utf-8", ".html"),
    CSS("text/css;charset=utf-8", ".css"),
    JS("application/javascript;charset=utf-8", ".js"),
    PLAIN("text/plain;charset=utf-8", null),
    SVG("image/svg+xml", ".svg");

    private final String mimeType;
    private final String extension;

    ContentType(final String mimeType, final String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static String fromPath(final String path) {
        final String extension = getExtension(path);
        return Arrays.stream(values())
                .filter(type -> type.extension != null && type.extension.equals(extension))
                .findFirst()
                .orElse(PLAIN)
                .mimeType;
    }

    public static boolean supports(final String path) {
        final String extension = getExtension(path);
        return Arrays.stream(values())
                .anyMatch(type -> type.extension != null && type.extension.equals(extension));
    }

    private static String getExtension(final String path) {
        final int dotIndex = path.lastIndexOf(".");
        return dotIndex == -1 ? null : path.substring(dotIndex);
    }
}
