package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMimeType {
    ALL("*", null),
    TEXT_HTML("text/html", "utf-8"),
    TEXT_CSS("text/css", "utf-8"),
    TEXT_JS("text/javascript", "utf-8"),
    IMG_SVG("image/svg+xml", null),
    OCTET_STREAM("application/octet-stream", null),
    ;

    private final String type;
    private final String charset;

    HttpMimeType(String type, String charset) {
        this.type = type;
        this.charset = charset;
    }

    public static HttpMimeType from(String fileExtension) {
        return Arrays.stream(values())
                .filter(http11Accept -> http11Accept.type.contains(fileExtension))
                .findFirst()
                .orElse(OCTET_STREAM);
    }

    public String asString() {
        if (charset == null) return type;
        return type + ";" + "charset=" + charset + " ";
    }
}
