package org.apache.coyote.util;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html", ".html"),
    ACCEPT_ALL("*/*", ".html"),
    CSS("text/css", ".css"),
    JS("text/javascript", ".js"),
    PLAIN("text/plain", ".txt"),
    JSON("application/json", ".json"),
    FORM_DATA("image/svg+xml", ".svg");

    private static final String ENCODING_TYPE = ";charset=utf-8";

    private final String contentType;
    private final String extension;

    ContentType(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public static ContentType findContentTypeByPath(String path) {
        return Arrays.stream(ContentType.values())
                .filter(bodyType -> path.contains(bodyType.extension))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 확장자입니다."));
    }

    public static ContentType findContentTypeByHeader(String headerContentType) {
        return Arrays.stream(ContentType.values())
                .filter(bodyType -> bodyType.contentType.equals(headerContentType))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 헤더 컨텐츠 타입입니다."));
    }

    public static boolean hasExtension(String path) {
        return Arrays.stream(ContentType.values())
                .anyMatch(type -> path.contains(type.extension));
    }

    public String getContentType() {
        return contentType + ENCODING_TYPE;
    }

    public String getExtension() {
        return extension;
    }
}
