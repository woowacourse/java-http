package org.apache.coyote.http11.message.common;

public enum ContentType {
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "text/javascript;charset=utf-8"),
    SVG(".svg", "image/svg+xml"),
    NONE("", "");

    private final String extension;
    private final String value;

    ContentType(String extension, String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType from(String uri) {
        if (uri.endsWith(HTML.extension)) {
            return HTML;
        }
        if (uri.endsWith(CSS.extension)) {
            return CSS;
        }
        if (uri.endsWith(JS.extension)) {
            return JS;
        }
        if (uri.endsWith(SVG.extension)) {
            return SVG;
        }
        return NONE;
    }

    public String getValue() {
        return value;
    }
}
