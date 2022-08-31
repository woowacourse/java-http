package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/javascript"),
    ICO("image/x-icon");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public static String of(String type) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.name().equalsIgnoreCase(type))
                .map(ContentType::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않은 ContentType 입니다."));
    }

    public String getValue() {
        return value;
    }
}
