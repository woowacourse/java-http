package org.apache.coyote.http11;

import java.util.List;

public enum HttpContentType {
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "application/javascript;charset=utf-8"),
    SVG(".svg", "image/svg+xml;charset=utf-8"),
    URL("", "application/x-www-form-urlencoded"),
    DEFAULT("", "text/plain;charset=utf-8"),
    ;

    private static final List<HttpContentType> STATIC_RESOURCE_TYPES = List.of(HTML, CSS, JS, SVG);

    private final String extension;
    private final String value;

    HttpContentType(final String extension, final String value) {
        this.extension = extension;
        this.value = value;
    }

    public static HttpContentType fromExtension(final String path) {
        return STATIC_RESOURCE_TYPES.stream()
                .filter(httpContentType -> path.endsWith(httpContentType.extension))
                .findFirst()
                .orElse(DEFAULT);
    }

    public static boolean isStaticResource(final String path) {
        return STATIC_RESOURCE_TYPES.stream()
                .anyMatch(httpContentType -> path.endsWith(httpContentType.extension));
    }

    public String getValue() {
        return value;
    }
}
