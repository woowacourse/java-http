package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum MimeType {

    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css;charset=utf-8"),
    JS("js", "application/javascript;charset=utf-8"),
    TXT("txt", "text/plain;charset=utf-8"),
    BINARY("", "application/octet-stream");

    private final String extension;
    private final String value;

    MimeType(String extension, String value) {
        this.extension = extension;
        this.value = value;
    }

    public static MimeType fromExtension(final String extension) {
        return Arrays.stream(MimeType.values())
                .filter(mimeType -> mimeType.extension.equals(extension))
                .findFirst()
                .orElse(BINARY);
    }

    public String getValue() {
        return value;
    }
}
