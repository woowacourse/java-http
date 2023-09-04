package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    ICON("ico", "image/x-icon"),
    SVG("svg", "image/svg+xml"),
    ;

    private String fileExtension;
    private String mimeType;

    ContentType(final String fileExtension, final String mimeType) {
        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
    }

    public static String findMimeType(final String extension) {
        return Arrays.stream(ContentType.values())
                .filter(value -> value.fileExtension.equals(extension))
                .map(value -> value.mimeType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 확장자명입니다."));
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getMimeType() {
        return mimeType;
    }
}
