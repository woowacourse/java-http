package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentMimeType {
    CSS("css", "text/css"),
    ICO("ico", "image/x-icon"),
    JS("js", "application.javascript"),
    HTML("html", "text/html"),
    SVG("svg", "image/svg+xml");

    private final String extension;
    private final String mime;

    ContentMimeType(String extension, String mime) {
        this.extension = extension;
        this.mime = mime;
    }

    public static String getMimeByExtension(String extension) {
        return Arrays.stream(ContentMimeType.values())
                .filter(type -> type.extension.equalsIgnoreCase(extension))
                .map(type -> type.mime)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 확장자입니다."));
    }

    public static boolean isEndsWithExtension(String uri) {
        return Arrays.stream(ContentMimeType.values())
                .anyMatch(type -> uri.endsWith(type.extension));
    }
}
