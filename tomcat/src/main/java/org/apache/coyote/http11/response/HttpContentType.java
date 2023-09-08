package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.start.HttpExtension;

import java.util.Arrays;

public enum HttpContentType {


    HTML("text/html;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    JS("text/javascript;charset=utf-8"),
    ICO("image/x-icon;charset=utf-8"),
    DEFAULT("Application/json;charset=utf-8");

    private final String contentType;
    HttpContentType(final String contentType) {
        this.contentType = contentType;
    }

    public static HttpContentType from(final HttpExtension httpExtension) {
        return Arrays.stream(HttpContentType.values())
                .filter(contentType -> contentType.toString().equalsIgnoreCase(httpExtension.toString()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청입니다."));
    }

    public String getContentType() {
        return contentType;
    }
}
