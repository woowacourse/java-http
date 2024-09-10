package org.apache.coyote.http11;

public enum MimeType {
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
    private final String mimeType;

    MimeType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static String toMimeType(final String extension) {
        for (MimeType mimeType : values()) {
            if (mimeType.getExtension().equalsIgnoreCase(extension)) {
                return mimeType.getMimeType();
            }
        }
        throw new IllegalArgumentException("Unknown mime type: " + extension);
    }

    public static boolean isValidType(final String uri) {
        for (MimeType mimeType : values()) {
            if (uri.endsWith(mimeType.getExtension())) {
                return true;
            }
        }
        return false;
    }

}
