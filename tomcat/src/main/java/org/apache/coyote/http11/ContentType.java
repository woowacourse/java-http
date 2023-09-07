package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    TEXT_HTML("text/html; charset=utf-8", ".html"),
    TEXT_CSS("text/css; charset=utf-8", ".css"),
    TEXT_JS("text/javascript; charset=utf-8", ".js"),
    SVG("image/svg+xml", ".svg");

    private final String value;
    private final String extension;

    ContentType(String value, String extension) {
        this.value = value;
        this.extension = extension;
    }

    public String value() {
        return value;
    }

    public String extension() {
        return extension;
    }

    public static ContentType findContentTypeByFilename(String filename) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> filename.endsWith(contentType.extension))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported extension"));
    }
}
