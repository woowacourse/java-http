package org.apache.coyote.http11.common;

public enum MimeType {

    HTML("text/html"),
    CSS("text/css"),
    JS("text/javascript"),
    SVG("image/svg+xml"),
    ICO("image/x-icon");

    private final String contentType;

    MimeType(String contentType) {
        this.contentType = contentType;
    }

    public static MimeType from(String fileExtension) {
        return MimeType.valueOf(fileExtension.toUpperCase());
    }

    public String getContentType() {
        return contentType;
    }
}
