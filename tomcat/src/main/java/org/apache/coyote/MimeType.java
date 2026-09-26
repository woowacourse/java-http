package org.apache.coyote;

import java.util.stream.Stream;

public enum MimeType {
    TEXT_HTML("text/html;charset=utf-8", "html"),
    TEXT_CSS("text/css;charset=utf-8", "css"),
    TEXT_JAVASCRIPT("text/javascript;charset=utf-8", "js"),
    IMAGE_SVG("image/svg+xml", "svg"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded", "form"),
    ;

    private static final char EXTENSION_DELIMITER = '.';

    private final String typeName;
    private final String expectedExtension;

    MimeType(String typeName, String expectedExtension) {
        this.typeName = typeName;
        this.expectedExtension = expectedExtension;
    }

    public static MimeType fromFileName(String fileName) {
        String extension = extensionOf(fileName);
        return Stream.of(values())
                .filter(mimeType -> mimeType.expectedExtension.equalsIgnoreCase(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 콘텐츠 유형입니다: " + fileName));
    }

    private static String extensionOf(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(EXTENSION_DELIMITER);
        if (lastIndexOfDot == -1) {
            return "";
        }
        return fileName.substring(lastIndexOfDot + 1);
    }

    public String getTypeName() {
        return typeName;
    }
}
