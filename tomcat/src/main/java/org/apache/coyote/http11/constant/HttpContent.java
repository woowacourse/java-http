package org.apache.coyote.http11.constant;

import java.util.Arrays;

public enum HttpContent {
    CSS("css", "text/css"),
    JAVASCRIPT("js", "application/javascript"),
    HTML("html", "text/html;charset=utf-8");

    private final String extension;
    private final String contentType;

    HttpContent(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static String extensionToContentType(String extension) {
        return Arrays.stream(values())
                .filter(value -> value.extension.equals(extension))
                .map(value -> value.contentType)
                .findAny()
                .orElse("text/plain");
    }

    public String getContentType() {
        return contentType;
    }
}
