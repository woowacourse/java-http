package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;

public enum ContentType {
    ALL(new String[]{"*/*"}, "all"),
    HTML(new String[]{"text/html"}, "html"),
    CSS(new String[]{"text/css"}, "css"),
    JS(new String[]{"application/javascript", "text/javascript"}, "js"),
    IMAGE_AVIF(new String[]{"image/avif"}, "avif"),
    ;

    private final List<String> mimeTypes;
    private final String extension;

    ContentType(String[] mimeTypes, String extension) {
        this.mimeTypes = Arrays.asList(mimeTypes);
        this.extension = extension;
    }

    public static ContentType fromHeaderValue(String headerValue) {
        for (ContentType contentType : ContentType.values()) {
            if (contentType.mimeTypes.contains(headerValue)) {
                return contentType;
            }
        }
        throw new IllegalArgumentException("잘못된 Content-Type입니다. : " + headerValue);
    }

    public static ContentType fromFileName(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("잘못된 파일명입니다.: " + fileName);
        }
        final var fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
        for (ContentType contentType : ContentType.values()) {
            if (contentType.extension.equalsIgnoreCase(fileExtension)) {
                return contentType;
            }
        }
        throw new IllegalArgumentException("알 수 없는 파일 확장자입니다: " + fileExtension);
    }

    public String getResponseHeader() {
        return "Content-Type: " + mimeTypes.getFirst() + ";charset=utf-8";
    }

    public String getExtension() {
        return extension;
    }
}
