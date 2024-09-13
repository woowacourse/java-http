package org.apache.coyote.http11.constants;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    GIF("gif", "image/gif"),
    SVG("svg", "image/svg+xml"),
    AVIF("avif", "image/avif"),
    WEBP("webp", "image/webp"),
    APNG("apng", "image/apng"),
    ASTERISK("*", "*/*");

    private static final String CHARSET_UTF_8 = "; charset=utf-8";

    private final String extension;
    private final String contentType;

    ContentType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public String getExtension() {
        return extension;
    }

    public String getContentTypeUtf8() {
        return contentType + CHARSET_UTF_8;
    }

    public static ContentType toContentType(final String fileExtension) {
        for (ContentType contentType : values()) {
            if (contentType.getExtension().equalsIgnoreCase(fileExtension)) {
                return contentType;
            }
        }
        throw new IllegalArgumentException("Unknown content type: " + fileExtension);
    }

    public static boolean isValidType(final String uri) {
        for (ContentType contentType : values()) {
            if (uri.endsWith(contentType.getExtension())) {
                return true;
            }
        }
        return false;
    }
}
