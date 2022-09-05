package org.apache.coyote.model;

import org.apache.coyote.exception.NotFoundContentTypeException;

import java.util.Arrays;
import java.util.Objects;

public enum ContentType {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    ICO("ico", "image/x-icon"),
    ;

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static String getType(String extension) {
        Objects.requireNonNull(extension);
        return Arrays.stream(values())
                .filter(it -> it.extension.equals(extension))
                .findAny()
                .orElseThrow(() -> new NotFoundContentTypeException("ContentType을 찾을 수 없습니다."))
                .type;
    }

    public String getExtension() {
        return extension;
    }
}
