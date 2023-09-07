package org.apache.coyote.http11.message;

import java.util.Arrays;

public enum ContentType {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript"),
    ICO(".ico", "image/x-icon");

    private final String extension;
    private final String headerValue;

    ContentType(final String extension, final String headerValue) {
        this.extension = extension;
        this.headerValue = headerValue;
    }

    public static ContentType findByFileName(final String fileName) {
        final String extension = getExtension(fileName);
        return findByExtension(extension);
    }

    public static String getExtension(final String fileName) {
        final int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        throw new IllegalArgumentException("파일 확장자가 존재하지 않습니다.");
    }

    public static ContentType findByExtension(final String extension) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.extension.equals(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 파일형식입니다."));
    }

    public String getExtension() {
        return extension;
    }

    public String getHeaderValue() {
        return headerValue;
    }
}
