package org.apache.coyote.http11;

import java.util.Arrays;

public enum MimeTypeMaker {
    CSS("css", "text/css"),
    HTML("html", "text/html"),
    JS("js", "application/javascript"),
    JSON("json", "application/json"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    GIF("gif", "image/gif");

    private final String extension;
    private final String mimeType;

    MimeTypeMaker(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static String getMimeTypeFromExtension(String extension) {
        return Arrays.stream(MimeTypeMaker.values())
                .filter(type -> type.extension.equalsIgnoreCase(extension))
                .map(type -> type.mimeType)
                .findFirst()
                .orElse("application/octet-stream");
    }
}
