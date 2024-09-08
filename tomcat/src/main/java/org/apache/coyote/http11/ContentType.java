package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    CSS("text/css;charset=utf-8", "css"),
    JS("application/javascript;charset=utf-8", "js"),
    HTML("text/html;charset=utf-8", "html"),
    PNG("image/png", "png"),
    JPG("image/jpeg", "jpeg"),
    ICO("image/x-icon", "ico")
    ;

    private final String contentType;
    private final String extension;

    ContentType(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public String getContentType() {
        return contentType;
    }

    public static ContentType getContentType(String extension) {
        return Arrays.stream(values())
                .filter(contentType1 -> contentType1.extension.equals(extension))
                .findAny()
                .orElseThrow();
    }
}
