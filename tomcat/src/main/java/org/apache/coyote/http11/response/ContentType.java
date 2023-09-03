package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {

    ICO("image/x-icon"),
    HTML("text/html; charset=utf-8"),
    CSS("text/css; charset=utf-8"),
    JS("text/javascript"),
    SVG("image/svg+xml");

    private final String contentType;

    ContentType(final String contentType) {
        this.contentType = contentType;
    }

    public static ContentType from(final String fileName) {
        return Arrays.stream(values())
                .filter(each -> fileName.endsWith("." + each.name().toLowerCase()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("그런 확장자는 지원하지 않아요 ~"));
    }

    public String getContentType() {
        return contentType;
    }
}
