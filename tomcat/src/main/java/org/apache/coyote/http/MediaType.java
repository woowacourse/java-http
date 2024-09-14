package org.apache.coyote.http;

import java.util.Arrays;

public enum MediaType {

    TEXT_HTML("text/html", ".html"),
    TEXT_CSS("text/css", ".css"),
    APPLICATION_JAVASCRIPT("application/javascript", ".js"),
    IMAGE_SVG("image/svg+xml", ".svg"),
    IMAGE_ICO("image/x-icon", ".ico"),
    PLAIN_TEXT("text/plain", ".txt"),
    ;

    private final String value;
    private final String extension;

    MediaType(String value, String extension) {
        this.value = value;
        this.extension = extension;
    }

    public static boolean isSupportedExtension(final String uri) {
        return Arrays.stream(values()).anyMatch(mediaType -> uri.endsWith(mediaType.extension));
    }

    public static MediaType findByExtension(final String uri) {
        return Arrays.stream(values())
                .filter(mediaType -> uri.endsWith(mediaType.extension))
                .findFirst()
                .orElse(PLAIN_TEXT);
    }

    public String value() {
        return value;
    }

    public String extension() {
        return extension;
    }
}
