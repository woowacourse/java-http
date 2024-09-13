package org.apache.coyote.http11;

import java.util.Arrays;

public enum FileType {
    HTML("text/html", ".html"),
    JAVASCRIPT("text/javascript", ".js"),
    CSS("text/css", ".css");

    private final String value;
    private final String extension;

    FileType(String value, String extension) {
        this.value = value;
        this.extension = extension;
    }

    public String getValue() {
        return value;
    }

    public static FileType parseFilenameExtension(final String path) {
        return Arrays.stream(FileType.values())
                .filter(fileType -> path.endsWith(fileType.extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 파일 확장자입니다."));
    }
}
