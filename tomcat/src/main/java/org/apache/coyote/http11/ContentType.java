package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    CSS(".css", "text/css;charset=utf-8"),
    HTML(".html", "text/html;charset=utf-8");

    private final String extension;
    private final String mimeType;

    ContentType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static String detectMimeType(String path) {
        if (path == null || path.isBlank()) {
            return HTML.mimeType;
        }
        return Arrays.stream(ContentType.values())
                .filter(type -> path.toLowerCase().endsWith(type.extension))
                .findFirst()
                .map(type -> type.mimeType)
                .orElse(HTML.mimeType); // TODO: 기본값 변경
    }
}
