package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JAVASCRIPT("text/javascript"),
    ICO("image/vnd.microsoft.icon");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public static ContentType of(String type) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.isMatch(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 ContentType 입니다."));
    }

    private boolean isMatch(String type) {
        return name().equalsIgnoreCase(type) || value.equals(type);
    }

    public String getValue() {
        return value;
    }
}
