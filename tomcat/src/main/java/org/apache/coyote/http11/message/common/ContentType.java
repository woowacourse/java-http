package org.apache.coyote.http11.message.common;

import java.util.Arrays;
import java.util.List;

public enum ContentType {
    HTTP(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "text/javascript;charset=utf-8"),
    SVG(".svg", "image/svg+xml");

    private final String extension;
    private final String value;

    ContentType(String extension, String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType from(String uri) {
        if (uri.endsWith(CSS.extension)) {
            return CSS;
        }
        if (uri.endsWith(JS.extension)) {
            return JS;
        }
        if (uri.endsWith(SVG.extension)) {
            return JS;
        }
        return HTTP;
    }

    public static List<String> getViewExtension() {
        return Arrays.stream(values()).map(type -> type.extension)
                .toList();
    }

    public String getValue() {
        return value;
    }
}
