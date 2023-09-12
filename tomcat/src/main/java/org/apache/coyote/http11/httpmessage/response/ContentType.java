package org.apache.coyote.http11.httpmessage.response;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JS("text/javascript", "js");

    private static final String EXTENSION_DELIMITER = "\\.";
    private static final int EXTENSION_INDEX = 1;

    private final String value;
    private final String extension;

    ContentType(final String value, final String extension) {
        this.value = value;
        this.extension = extension;
    }

    public static ContentType find(final String path) {
        final String extension = path.split(EXTENSION_DELIMITER)[EXTENSION_INDEX];
        return Arrays.stream(ContentType.values())
            .filter(type -> type.extension.equals(extension))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 확장자 입니다."));
    }

    public String getValue() {
        return value;
    }
}
