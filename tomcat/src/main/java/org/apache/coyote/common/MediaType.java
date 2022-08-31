package org.apache.coyote.common;

import java.util.Arrays;

public enum MediaType {

    TEXT_HTML("text/html", FileExtension.HTML),
    TEXT_CSS("text/css", FileExtension.CSS)
    ;

    private final String value;
    private final FileExtension fileExtension;

    MediaType(final String value, final FileExtension fileExtension) {
        this.value = value;
        this.fileExtension = fileExtension;
    }

    public String getValue() {
        return value;
    }

    public static MediaType of(final FileExtension fileExtension) {
        return Arrays.stream(values())
                .filter(mediaType -> mediaType.fileExtension.equals(fileExtension))
                .findFirst()
                .orElse(TEXT_HTML);
    }
}
