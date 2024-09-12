package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum MimeType {

    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript"),
    ICO(".ico", "image/vnd.microsoft.icon"),
    JSON(".json","application/json");

    private final String extension;
    private final String type;

    MimeType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static MimeType find(String filePath) {
        return Arrays.stream(values())
                .filter(value -> filePath.endsWith(value.extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 HTTP MIME 타입입니다."));
    }

    public String getType() {
        return this.type;
    }
}
