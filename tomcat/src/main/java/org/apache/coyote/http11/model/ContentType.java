package org.apache.coyote.http11.model;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JAVASCRIPT("application/javascript", "js");

    private final String extension;
    private final String type;

    ContentType(final String type, final String extension) {
        this.type = type;
        this.extension = extension;
    }

    public static ContentType of(final String input) {
        return Arrays.stream(values())
                .filter(i -> input.contains(i.extension))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("알맞는 확장자가 없습니다."));
    }

    public String getType() {
        return type;
    }
}
