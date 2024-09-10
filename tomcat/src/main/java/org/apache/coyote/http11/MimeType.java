package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.util.Arrays;

public enum MimeType {

    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JS("text/js");

    public static final String EXTENSION_DELIMITER = ".";
    private final String value;

    MimeType(String value) {
        this.value = value;
    }

    public static MimeType from(String name) {
        return Arrays.stream(values())
                .filter(mimeType -> mimeType.name().equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(
                        () -> new UncheckedServletException(new IllegalArgumentException("유효한 Mime Type 이 아닙니다.")));
    }

    public static MimeType fromFileName(String fileName) {
        return Arrays.stream(values())
                .filter(mimeType -> mimeType.name().equalsIgnoreCase(extractExtension(fileName)))
                .findAny()
                .orElseThrow(() -> new UncheckedServletException(new IllegalArgumentException("유효한 파일 확장자가 아닙니다.")));
    }

    private static String extractExtension(String fileName) {
        int index = fileName.indexOf(EXTENSION_DELIMITER);
        return fileName.substring(index + 1);
    }

    public String value() {
        return value;
    }
}
