package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.exception.UncheckedHttpException;

public enum MediaType {

    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css"),
    JAVASCRIPT(".js", "application/javascript"),
    JSON(".json", "application/json"),
    PLAIN_TEXT(".txt", "text/plain");

    private final String extension;
    private final String value;

    MediaType(String extension, String value) {
        this.extension = extension;
        this.value = value;
    }

    public static MediaType from(String extension) {
        return Arrays.stream(values())
                .filter(value -> value.extension.equals(extension))
                .findFirst()
                .orElseThrow(
                        () -> new UncheckedHttpException(new IllegalArgumentException("처리할 확장자가 없습니다. 값: " + extension))
                );
    }

    public String getExtension() {
        return extension;
    }

    public String getValue() {
        return value;
    }
}
