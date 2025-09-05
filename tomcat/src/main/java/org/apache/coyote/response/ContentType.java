package org.apache.coyote.response;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JS("text/javascript", "js"),
    PLAIN("text/plain", "txt")
    ;

    private final String contentType;
    private final String extension;

    ContentType(final String contentType, final String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public String getContentType() {
        return contentType;
    }

    public static ContentType findContentType(String extension) {
        return Arrays.stream(ContentType.values())
                .filter(bodyType -> bodyType.extension.equals(extension))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 처리할 수 없는 확장자입니다."));
    }
}
