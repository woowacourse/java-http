package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpContentType {
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "application/javascript;charset=utf-8"),
    SVG(".svg", "image/svg+xml;charset=utf-8"),
    DEFAULT("", "text/plain;charset=utf-8"),
    ;

    private final String extension;
    private final String value;

    HttpContentType(final String extension, final String value) {
        this.extension = extension;
        this.value = value;
    }

    public static HttpContentType fromExtension(final String path) {
        return Arrays.stream(HttpContentType.values())
                .filter(httpContentType -> path.endsWith(httpContentType.extension))
                .findFirst()
                .orElse(DEFAULT);
    }

    public String getValue() {
        return value;
    }
}
