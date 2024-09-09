package com.techcourse.http;

import lombok.Getter;

@Getter
public enum MimeType {
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css"),
    JS(".js", "application/javascript"),
    JSON(".json", "application/json"),
    PNG(".png", "image/png"),
    JPG(".jpg", "image/jpeg"),
    JPEG(".jpeg", "image/jpeg"),
    GIF(".gif", "image/gif"),
    DEFAULT("", "application/octet-stream");

    private final String extension;
    private final String mimeType;

    MimeType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static String from(String fileName) {
        for (MimeType type : values()) {
            if (fileName.endsWith(type.extension)) {
                return type.mimeType;
            }
        }
        return DEFAULT.mimeType;
    }
}
