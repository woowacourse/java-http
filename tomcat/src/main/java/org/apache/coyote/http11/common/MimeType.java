package org.apache.coyote.http11.common;

import java.io.File;

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

    public static MimeType from(File file) {
        if (file == null) {
            return HTML;
        }
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

        return MimeType.valueOf(fileExtension.toUpperCase());
    }

    public String getContentType() {
        return contentType;
    }
}
