package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JAVASCRIPT("js", "text/javascript"),
    SVG("svg", "image/svg+xml");

    private final String extension;
    private final String value;

    ContentType(final String extension, final String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType findByUri(final String uri) {
        return Arrays.stream(values())
                .filter(it -> uri.endsWith(it.extension))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("파일 확장자를 찾을 수 없습니다."));
    }

    public String getValue() {
        return value;
    }
}
