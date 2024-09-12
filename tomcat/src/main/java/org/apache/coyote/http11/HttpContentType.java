package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpContentType {
    TEXT(".html", "text/html"),
    JAVASCRIPT(".js", "text/javascript"),
    CSS(".css", "text/css"),
    SVG(".svg", "image/svg+xml"),
    DEFAULT("", "text/html");

    private final String extension;
    private final String contentType;

    HttpContentType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static HttpContentType matchContentType(String path) {
        String extension = parseExtension(path);
        return Arrays.stream(values())
                .filter(httpContentType -> httpContentType.extension.equals(extension))
                .findFirst()
                .orElse(DEFAULT);
    }

    private static String parseExtension(String path) {
        int extensionIndex = path.lastIndexOf(".");
        if (extensionIndex == -1) {
            return "";
        }
        return path.substring(extensionIndex);
    }

    public String getContentType() {
        return contentType;
    }
}
