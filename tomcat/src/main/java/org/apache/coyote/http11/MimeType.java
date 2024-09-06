package org.apache.coyote.http11;

import java.util.Arrays;

public enum MimeType {
    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css"),
    SVG("svg", "image/svg+xml"),
    NONE("", "application/octet-stream");

    private final String extension;
    private final String type;

    MimeType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static MimeType getMimeType(String fileName) {
        String extension = extractExtension(fileName);
        return Arrays.stream(MimeType.values())
                .filter(mimeType -> mimeType.isSameExtension(extension))
                .findFirst()
                .orElse(NONE);
    }

    private boolean isSameExtension(String extension) {
        return this.extension.equals(extension);
    }

    private static String extractExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return "";
        }
        return fileName.substring(index + 1);
    }

    public String getType() {
        return type;
    }

    public String getExtension() {
        return extension;
    }
}
