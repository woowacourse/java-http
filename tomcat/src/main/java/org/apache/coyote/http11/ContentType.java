package org.apache.coyote.http11;

import java.util.Map;

public enum ContentType {
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript"),
    TEXT_PLAIN("text/plain"),
    IMAGE_X_ICON("image/x-icon"),
    IMAGE_GIF("image/gif"),
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_SVG("image/svg");

    private final static Map<String, ContentType> fileExtensionMapping = Map.of(
            ".html", TEXT_HTML,
            ".css", TEXT_CSS,
            ".js", TEXT_JAVASCRIPT,
            ".ico", IMAGE_X_ICON,
            ".gif", IMAGE_GIF,
            ".png", IMAGE_PNG,
            ".jpeg", IMAGE_JPEG,
            ".svg", IMAGE_SVG
    );

    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    public static ContentType fromFilePath(final String filePath) {
        final String fileExtension = fileExtensionMapping.keySet()
                .stream()
                .filter(filePath::endsWith)
                .findFirst()
                .orElseThrow(() ->
                        new UnsupportedOperationException(String.format("지원하지 않는 파일 확장자입니다. %s", filePath))
                );

        return fileExtensionMapping.get(fileExtension);
    }

    public String getValue() {
        return value;
    }
}
