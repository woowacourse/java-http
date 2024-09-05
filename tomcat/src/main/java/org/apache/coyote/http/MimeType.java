package org.apache.coyote.http;

import java.util.Arrays;

public enum MimeType {
    HTML("text/html;charset=utf-8", ".html"),
    CSS("text/css", ".css"),
    JS("application/javascript;charset=utf-8", ".js"),
    TEXT("text/plain;charset=utf-8", ".txt"),
    ;

    private final String contentType;
    private final String extension;

    MimeType(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public static String getContentTypeFromExtension(String extension) {
        return Arrays.stream(MimeType.values()).filter(mimeType -> extension.contains(mimeType.extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(extension + " didn't match"))
                .contentType;
    }
}
