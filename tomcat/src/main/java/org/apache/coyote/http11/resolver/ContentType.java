package org.apache.coyote.http11.resolver;

public enum ContentType {
    HTML("text/html;charset=utf-8", ".html"),
    CSS("text/css;charset=utf-8", ".css"),
    JS("application/javascript;charset=utf-8", ".js"),
    SVG("image/svg+xml;charset=utf-8", ".svg"),
    PNG("image/png", ".png"),
    JPG("image/jpeg", ".jpg"),
    ICO("image/x-icon", ".ico"),
    PLAIN("text/plain;charset=utf-8", null);

    private final String contentType;
    private final String extension;

    ContentType(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public static String from(String path) {
        for (ContentType type : values()) {
            if (type.extension != null && path.endsWith(type.extension)) {
                return type.contentType;
            }
        }
        return PLAIN.contentType;
    }
}
