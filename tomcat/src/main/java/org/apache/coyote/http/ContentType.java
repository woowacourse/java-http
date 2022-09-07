package org.apache.coyote.http;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    ICO("ico", "image/x-icon"),
    SVG("svg", "image/svg"),
    ;

    private final String extension;
    private final String contentType;

    ContentType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ContentType findContentType(final String url) {
        return Arrays.stream(values())
                .filter(value -> url.endsWith(value.extension))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("확장자가 적절하지 않습니다."));
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }
}
