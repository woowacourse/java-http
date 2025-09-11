package org.apache.coyote.http;

import com.techcourse.exception.NotFoundException;
import java.util.Arrays;
import java.util.Objects;

public enum ContentType {

    TEXT_HTML("html", "text/html;charset=utf-8"),
    TEXT_CSS("css", "text/css;charset=utf-8"),
    APPLICATION_JAVASCRIPT("js", "application/javascript;charset=utf-8"),
    IMAGE_X_ICON("ico", "image/x-icon"),
    IMAGE_SVG_XML("svg", "image/svg+xml"), // XML 이라 charset 지정 가능하긴 하지만 보통 생략
    APPLICATION_JSON("json", "application/json;charset=utf-8"),
    ;

    private final String extension;
    private final String mediaType;

    ContentType(final String extension, final String mediaType) {
        this.extension = extension;
        this.mediaType = mediaType;
    }

    public static ContentType from(final String extension) {
        Objects.requireNonNull(extension);
        String normalized = extension.trim()
                .toLowerCase()
                .replaceFirst("^\\.", "");

        return Arrays.stream(values())
                .filter(value -> value.extension.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("존재하지 않는 Content type 확장자입니다: " + normalized));
    }

    public String toHeaderLine() {
        return "Content-Type: " + mediaType;
    }
}
