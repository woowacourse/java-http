package org.apache.coyote.http11;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("application/javascript"),
    ICO("image/x-icon"),
    PNG("image/png"),
    WOFF("font/woff"),
    WOFF2("font/woff2");

    private final String type;

    ContentType(final String type) {
        this.type = type;
    }

    public static ContentType of(final String extension) {
        for (ContentType contentType : values()) {
            if (contentType.name().equalsIgnoreCase(extension)) {
                return contentType;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 확장자입니다.");
    }

    public String getType() {
        return type;
    }
}
