package org.apache.coyote.http;

import java.util.Arrays;

public enum ContentType {
    CSS(".css", "text/css"),
    ICO(".ico", "text/css"),
    JS(".js", "text/javascript"),
    SVG(".svg", "image/svg+xml"),
    HTML(".html", "text/html; charset=utf-8");

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static String findType(String path) {
        return Arrays.stream(values())
                .filter(contentType -> path.endsWith(contentType.extension))
                .map(contentType -> contentType.type)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 Content Type 입니다."));
    }

    public static boolean isStaticFile(String type) {
        return Arrays.stream(values())
                .anyMatch(contentType -> contentType.getType().equals(type));
    }

    public String getType() {
        return type;
    }
}
