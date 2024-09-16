package org.apache.coyote.http11.constant;

import java.util.Arrays;

public enum MimeType {

    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript"),
    ICO(".ico", "image/vnd.microsoft.icon"),
    JSON(".json","application/json"),
    SVG(".svg","image/svg+xml"),
    ;

    private final String extension;
    private final String type;

    MimeType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static MimeType find(String filePath) {
        validateNull(filePath);
        return Arrays.stream(values())
                .filter(value -> filePath.endsWith(value.extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 파일 경로입니다."));
    }

    private static void validateNull(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 파일 경로입니다.");
        }
    }

    public String getType() {
        return this.type;
    }
}
