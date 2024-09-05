package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    CSS("text/css;charset=utf-8", "css"),
    JS("application/javascript;charset=utf-8", "js"),
    HTML("text/html;charset=utf-8", "html"),
    PNG("image/png", "png"),
    JPG("image/jpeg", "jpeg")
    ;

    private final String contentType;
    private final String extention;

    ContentType(String contentType, String extention) {
        this.contentType = contentType;
        this.extention = extention;
    }

    public String getContentType() {
        return contentType;
    }

    public static ContentType getContentType(String extention) {
        return Arrays.stream(values())
                .filter(contentType1 -> contentType1.extention.equals(extention))
                .findAny()
                .orElseThrow();
    }
}
