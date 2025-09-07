package org.apache.coyote.http11;

import java.util.Arrays;

public enum MimeType {
    TEXT_HTML("text/html", ".html"),
    TEXT_CSS("text/css",  ".css"),
    APPLICATION_JAVASCRIPT("application/javascript", ".js");

    private static final String CONTENT_TYPE_FORMAT = "%s;%s";
    private static final String ENCODING_PARAMETER = "charset=utf-8";

    private final String value;
    private final String extension;

    MimeType(String value, String extension) {
        this.value = value;
        this.extension = extension;
    }

    public static MimeType getMimeType(String extension) {
        return Arrays.stream(values())
                .filter(type -> type.extension.equalsIgnoreCase(extension))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String getContentType() {
        return String.format(CONTENT_TYPE_FORMAT, value, ENCODING_PARAMETER);
    }
}
