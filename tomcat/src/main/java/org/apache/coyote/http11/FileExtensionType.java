package org.apache.coyote.http11;

import java.util.Arrays;

public enum FileExtensionType {
    HTML("html"),
    CSS("css"),
    JAVASCRIPT("js"),
    SVG("svg"),
    ICO("ico");

    private final String value;

    FileExtensionType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FileExtensionType fromValue(final String input) {
        return Arrays.stream(FileExtensionType.values())
                .filter(value -> value.getValue().equals(input))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 파일 확장자 타입입니다. : " + input));
    }
}
