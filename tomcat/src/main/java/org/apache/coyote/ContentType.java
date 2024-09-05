package org.apache.coyote;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html;charset=utf-8 "),
    JS("js", "text/javascript;charset=utf-8"),
    CSS("css", "text/css;charset=utf-8");

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static String getContentType(String extension) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.extension.equalsIgnoreCase(extension))
                .findFirst()
                .map(contentType -> contentType.type)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 확장자입니다."));
    }
}
