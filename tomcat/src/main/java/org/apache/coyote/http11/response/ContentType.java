package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript"),
    SVG(".svg", "image/svg+xml"),
    ICO(".ico", "image/x-icon"),
    PNG(".png", "image/png"),
    JPEG(".jpg", "image/jpeg"),
    ;

    private static final ContentType DEFAULT = HTML;
    private static final String CHARSET_SUFFIX = ";charset=utf-8";

    private final String extension;
    private final String mimeType;

    ContentType(final String extension, final String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static ContentType from(final String path) {
        return Arrays.stream(values())
                .filter(contentType -> path.endsWith(contentType.extension))
                .findFirst()
                .orElse(DEFAULT);
    }

    public String getValue() {
        return mimeType + CHARSET_SUFFIX;
    }
}
