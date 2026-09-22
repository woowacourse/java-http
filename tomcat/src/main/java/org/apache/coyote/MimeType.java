package org.apache.coyote;

import java.util.Arrays;

public enum MimeType {

    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JAVASCRIPT(".js", "application/javascript;charset=utf-8"),
    SVG(".svg", "image/svg+xml");

    private final String extension;
    private final String contentType;

    MimeType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static String from(String path) {
        return Arrays.stream(values())
                .filter(mimeType -> path.endsWith(mimeType.extension))
                .findFirst()
                .map(mimeType -> mimeType.contentType)
                .orElse(HTML.contentType);
    }
}
