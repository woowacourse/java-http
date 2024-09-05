package org.apache.coyote.common;

import java.io.File;
import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css;charset=utf-8"),
    JS("js", "text/javascript;charset=utf-8"),
    ICO("ico", "image/x-icon"),
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png"),
    SVG("svg", "image/svg+xml"),
    GIF("gif", "image/gif"),
    PDF("pdf", "application/pdf"),
    JSON("json", "application/json"),
    XML("xml", "application/xml"),
    PLAIN("plain", "text/plain;charset=utf-8");

    private final String fileExtension;
    private final String mimeType;

    ContentType(String fileExtension, String mimeType) {
        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
    }

    public static ContentType of(File resource) {
        return Arrays.stream(ContentType.values())
                .filter(type -> resource.getName().endsWith(type.fileExtension))
                .findFirst()
                .orElse(PLAIN);
    }

    public String getMimeType() {
        return mimeType;
    }
}