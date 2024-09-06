package org.apache.coyote.http11.message.common;

import java.util.Arrays;

public enum FileExtension {

    HTML("html", ContentType.TEXT_HTML),
    JS("js", ContentType.TEXT_JS),
    CSS("css", ContentType.TEXT_CSS),
    ;

    private final String extension;
    private final ContentType contentType;

    FileExtension(String extension, ContentType contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static FileExtension getFileExtension(String extension) {
        return Arrays.stream(values())
                .filter(fileExtension -> fileExtension.extension.equalsIgnoreCase(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 확장자입니다."));
    }

    public ContentType getContentType() {
        return contentType;
    }
}
