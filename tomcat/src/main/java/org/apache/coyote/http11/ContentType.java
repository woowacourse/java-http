package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    TEXT_HTML("text/html", "html", true),
    TEXT_CSS("text/css", "css", true),
    APPLICATION_JAVASCRIPT("application/javascript", "js", true),
    ;

    private final String contentType;
    private final String fileExtension;
    private final boolean isTextContent;

    ContentType(String contentType, String fileExtension, boolean isTextContent) {
        this.contentType = contentType;
        this.fileExtension = fileExtension;
        this.isTextContent = isTextContent;
    }

    public String getContentType() {
        if (isTextContent) {
            return contentType + ";charset=utf-8";
        }
        return contentType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public static ContentType findByFileExtension(String fileExtension) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> fileExtension.equals(contentType.fileExtension))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 Content Type입니다."));
    }
}
