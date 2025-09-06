package org.apache.coyote.http11.message.response;

import java.util.Arrays;

public enum ContentType {
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "application/javascript;charset=utf-8"),
    PNG(".png", "image/png"),
    JPG(".jpg", "image/jpeg"),
    JPEG(".jpeg", "image/jpeg"),
    GIF(".gif", "image/gif"),
    DEFAULT(null, "application/octet-stream");

    private final String extension;
    private final String mimeType;

    ContentType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static String getMimeTypeFrom(String path) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.extension != null && path.endsWith(contentType.extension))
                .findFirst()
                .orElse(DEFAULT)
                .getMimeType();
    }
}
