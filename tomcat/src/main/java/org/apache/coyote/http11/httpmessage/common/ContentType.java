package org.apache.coyote.http11.httpmessage.common;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JS("text/javascript", "js"),
    FAVICON("image/avif", "ico"),
    DEFAULT("*/*", "");

    final String contentType;
    final String fileExtension;

    ContentType(String contentType, String fileExtension) {
        this.contentType = contentType;
        this.fileExtension = fileExtension;
    }

    public static ContentType from(String fileExtension) {
        return Arrays.stream(values()).filter(it -> it.isSameFileExtension(fileExtension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 content type입니다."));
    }

    public String getContentType() {
        return contentType;
    }

    private boolean isSameFileExtension(String target) {
        return this.fileExtension.equals(target);
    }
}
