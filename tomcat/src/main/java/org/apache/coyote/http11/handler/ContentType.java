package org.apache.coyote.http11.handler;

import java.util.Locale;

public enum ContentType {

    TEXT("text/plain;charset=utf-8"),
    HTML("text/html;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    JAVASCRIPT("application/javascript"),
    SVG("image/svg+xml"),
    PNG("image/png"),
    JPG("image/jpeg"),
    DEFAULT("*/*;charset=utf-8"),
    ;

    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    public static ContentType fromPath(final String path) {
        final String lower = path.toLowerCase(Locale.ROOT);

        if (lower.endsWith(".html") || lower.endsWith(".htm")) {
            return HTML;
        }
        if (lower.endsWith(".css")) {
            return CSS;
        }
        if (lower.endsWith(".js")) {
            return JAVASCRIPT;
        }
        if (lower.endsWith(".svg")) {
            return SVG;
        }
        if (lower.endsWith(".png")) {
            return PNG;
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return JPG;
        }
        if (lower.endsWith(".txt")) {
            return TEXT;
        }
        return DEFAULT;
    }

    public String value() {
        return value;
    }
}
