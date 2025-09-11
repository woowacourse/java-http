package org.apache.coyote.http11.dto;

import static org.apache.coyote.http11.HttpConstants.PERIOD;

import java.util.Arrays;
import java.util.List;

public enum ContentType {

    TEXT("text/plain;charset=utf-8", List.of("txt")),
    HTML("text/html;charset=utf-8", List.of("html", "htm")),
    CSS("text/css;charset=utf-8", List.of("css")),
    JAVASCRIPT("application/javascript", List.of("js")),
    SVG("image/svg+xml", List.of("svg")),
    PNG("image/png", List.of("png")),
    JPG("image/jpeg", List.of("jpg", "jpeg")),
    DEFAULT("*/*;charset=utf-8", List.of("*")),
    ;

    private final String value;
    private final List<String> extensions;

    ContentType(final String value, final List<String> extensions) {
        this.value = value;
        this.extensions = extensions;
    }

    public static ContentType fromPath(final String path) {
        final String lower = path.toLowerCase();
        return Arrays.stream(ContentType.values())
                .filter(type -> type.extensions.stream()
                        .anyMatch(ext -> lower.endsWith(PERIOD + ext)))
                .findFirst()
                .orElse(DEFAULT);
    }

    public String value() {
        return value;
    }

    public List<String> extensions() {
        return extensions;
    }
}
