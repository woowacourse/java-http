package com.techcourse.http.common;

import com.techcourse.exception.NotFoundException;
import java.util.Arrays;
import java.util.Objects;

public enum ContentType {

    TEXT_HTML("html", "text/html"),
    TEXT_CSS("css", "text/css"),
    APPLICATION_JAVASCRIPT("js", "application/javascript"),
    IMAGE_SVG_XML("svg", "image/svg+xml"),
    ;

    private final String extension;
    private final String mediaType;

    ContentType(final String extension, final String mediaType) {
        this.extension = extension;
        this.mediaType = mediaType;
    }

    public static ContentType from(final String extension) {
        Objects.requireNonNull(extension);
        return Arrays.stream(values())
                .filter(value -> value.extension.equals(extension))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("존재하지 않는 Content type 확장자입니다."));
    }

    public String getMediaType() {
        return mediaType;
    }
}
