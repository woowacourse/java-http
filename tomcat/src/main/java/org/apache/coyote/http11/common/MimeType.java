package org.apache.coyote.http11.common;

public enum MimeType {

    NONE("text/plain"),
    HTML("text/html"),
    CSS("text/css"),
    JS("text/javascript"),
    ICO("image/x-icon");

    private final String contentType;

    MimeType(String contentType) {
        this.contentType = contentType;
    }

    public static MimeType from(String fileName) {
        if (fileName == null) {
            return NONE;
        }

        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

        return MimeType.valueOf(fileExtension.toUpperCase());
    }

    public String getContentType() {
        return contentType;
    }
}
