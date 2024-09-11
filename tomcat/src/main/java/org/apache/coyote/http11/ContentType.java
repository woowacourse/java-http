package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    TEXT_HTML("text/html;charset=utf-8", "html", true),
    TEXT_CSS("text/css;charset=utf-8", "css", true),
    APPLICATION_JAVASCRIPT("application/javascript;charset=utf-8", "js", true),
    ;

    private String value;
    private String fileExtension;
    private boolean isTextContent;

    ContentType(String value, String fileExtension, boolean isTextContent) {
        this.value = value;
        this.fileExtension = fileExtension;
        this.isTextContent = isTextContent;
    }

    public String getContentType() {
        if (isTextContent) {
            return value + ";charset=utf-8";
        }
        return value;
    }

    public static ContentType findByFileExtension(String fileExtension) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> fileExtension.equals(contentType.fileExtension))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 Content Type입니다."));
    }
}
