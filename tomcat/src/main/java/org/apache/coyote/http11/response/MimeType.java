package org.apache.coyote.http11.response;

import java.util.Arrays;
import java.util.Objects;

public enum MimeType {
    TEXT_HTML("html", "text/html;charset=utf-8"),
    TEXT_CSS("css", "text/css;charset=utf-8"),
    APPLICATION_JAVASCRIPT("js", "application/javascript;charset=utf-8"),
    TEXT_PLAIN("txt", "text/plain;charset=utf-8");

    private final String extension;
    private final String value;

    MimeType(String extension, String value) {
        this.extension = extension;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MimeType from(String extension) {
        return Arrays.stream(values())
                .filter(mimeType -> Objects.equals(mimeType.extension, extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 파일 타입입니다: "  + extension));
    }
}
