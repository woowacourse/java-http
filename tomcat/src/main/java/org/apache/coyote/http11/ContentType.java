package org.apache.coyote.http11;

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
    ASTERISK("*", "*/*")
    ;

    private final String extension;
    private final String contentType;

    ContentType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }

    public static String toContentType(final String extension) {
        for (ContentType contentType : values()) {
            if (contentType.getExtension().equalsIgnoreCase(extension)) {
                return contentType.getContentType();
            }
        }
        throw new IllegalArgumentException("Unknown content type: " + extension);
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
