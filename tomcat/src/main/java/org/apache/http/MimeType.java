package org.apache.http;

import java.util.Arrays;

public enum MimeType {
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_JS("text/javascript"),
    TEXT_PLAIN("text/plain"),
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    APPLICATION_XHTML("application/xhtml+xml"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    IMAGE_JPEG("image/jpeg"),

    ;
    private final String value;

    MimeType(String value) {
        this.value = value;
    }

    public static MimeType getMimeType(String value) {
        return Arrays.stream(values())
                .filter(mimeType -> mimeType.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 MimeType 입니다."));
    }

    public String getValue() {
        return value;
    }
}
