package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Locale;

public enum ContentType {

    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css;charset=utf-8"),
    JS("js", "text/javascript;charset=utf-8"),
    SVG("svg", "image/svg+xml"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    GIF("gif", "image/gif"),
    ICO("ico", "image/x-icon"),
    DEFAULT("", "application/octet-stream");

    private final String extension;
    private final String value;

    ContentType(final String extension, final String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType from(final String path) {
        final String fileName = path.substring(path.lastIndexOf('/') + 1);
        final int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return DEFAULT;
        }
        final String extension = fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(type -> type.extension.equals(extension))
                .findFirst()
                .orElse(DEFAULT);
    }

    public String getValue() {
        return value;
    }
}