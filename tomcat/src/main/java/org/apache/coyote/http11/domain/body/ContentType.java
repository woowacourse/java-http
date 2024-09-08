package org.apache.coyote.http11.domain.body;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JS("text/javascript", "js"),
    PLAIN("text/plain", "txt"),
    FORM_DATA("multipart/formed-data", "svg"),
    ICO("multipart/formed-data", "ico");

    private final String contentType;
    private final String extension;

    ContentType(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public static ContentType findContentType(String extension) {
        return Arrays.stream(ContentType.values())
                .filter(bodyType -> bodyType.extension.equals(extension))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 확장자입니다."));
    }

    public String getContentType() {
        return contentType;
    }
}
