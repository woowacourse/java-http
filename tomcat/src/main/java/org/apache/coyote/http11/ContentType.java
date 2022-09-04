package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.support.FileUtils;

public enum ContentType {

    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript"),
    TEXT_PLAIN("text/plain"),
    IMAGE_X_ICON("image/x-icon"),
    IMAGE_GIF("image/gif"),
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_SVG("image/svg+xml");

    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    public static ContentType fromFilePath(final String filePath) {
        final String mimeType = FileUtils.getMimeTypeFromPath(filePath);
        return Arrays.stream(values()).filter(contentType -> contentType.value.equals(mimeType))
                .findAny()
                .orElseThrow(() ->
                        new UnsupportedOperationException(String.format("지원하지 않는 파일 확장자입니다. %s", filePath))
                );
    }

    public String getValue() {
        return value;
    }
}
