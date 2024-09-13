package org.apache.coyote.http11;

import java.util.Arrays;

public enum MimeType {

    HTML("text/html;charset=utf-8", "html"),
    CSS("text/css", "css"),
    JS("application/javascript", "js"),
    SVG("image/svg+xml", "svg"),
    NONE("text/html;charset=utf-8", "");

    private final String type;
    private final String fileExtension;

    MimeType(String type, String fileExtension) {
        this.type = type;
        this.fileExtension = fileExtension;
    }

    public static MimeType from(String path) {
        return Arrays.stream(values())
                .filter(value -> path.endsWith(value.fileExtension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 파일 경로 요청 : " + path));
    }

    public static boolean isValidFileExtension(String path) {
        for (MimeType type : MimeType.values()) {
            if (path.endsWith(type.fileExtension) && !type.equals(NONE)) {
                return true;
            }
        }
        return false;
    }

    public String getType() {
        return type;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
