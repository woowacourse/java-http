package org.apache.coyote.http11;

public enum FileExtension {

    NONE("text/plain"),
    HTML("text/html"),
    CSS("text/css"),
    JS("text/javascript"),
    ICO("image/x-icon");

    private final String contentType;

    FileExtension(String contentType) {
        this.contentType = contentType;
    }

    public static FileExtension from(String fileExtension) {
        if (fileExtension == null) {
            return NONE;
        }
        return FileExtension.valueOf(fileExtension.toUpperCase());
    }

    public String getContentType() {
        return contentType;
    }
}
