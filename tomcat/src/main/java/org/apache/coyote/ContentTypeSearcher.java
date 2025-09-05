package org.apache.coyote;

import java.util.Arrays;

public enum ContentTypeSearcher {

    HTML("html", "text/html;charset=utf-8 "),
    CSS("css", "text/css;charset=utf-8 "),
    JS("js", "application/javascript;charset=utf-8 "),
    JSON("json", "application/json;charset=utf-8 "),
    TXT("txt", "text/plain;charset=utf-8 "),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    GIF("gif", "image/gif"),
    SVG("svg", "image/svg+xml"),
    ICO("ico", "image/x-icon"),

    // 기본값
    NOTHING("", "text/html;charset=utf-8 ");

    private final String extension;
    private final String contentType;

    ContentTypeSearcher(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }

    public static String getContentTypeBy(String path) {
        int dot = path.lastIndexOf('.');
        String extension;
        if (dot >= 0 && dot < path.length() - 1) {
            extension = path.substring(dot + 1).toLowerCase();
        } else {
            extension = "";
        }

        return Arrays.stream(values())
                .filter(ct -> ct.extension.equals(extension))
                .findFirst()
                .orElse(NOTHING)
                .getContentType();
    }
}
