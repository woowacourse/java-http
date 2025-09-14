package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html;charset=utf-8", ".html", false),
    CSS("text/css;charset=utf-8", ".css", false),
    JS("application/javascript;charset=utf-8", ".js", false),
    PLAIN("text/plain;charset=utf-8", null, false),
    SVG("image/svg+xml", ".svg", true),
    JPG("image/jpeg", ".jpg", true),
    JPEG("image/jpeg", ".jpeg", true);

    private final String mimeType;
    private final String extension;
    private final boolean binary;

    ContentType(String mimeType, String extension, boolean binary) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.binary = binary;
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
