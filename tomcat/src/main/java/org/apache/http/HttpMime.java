package org.apache.http;

import nextstep.jwp.exception.CustomNotFoundException;

import java.util.Arrays;

public enum HttpMime {

    DEFAULT("*/*"),
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript"),
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    ;

    private final String value;

    HttpMime(final String value) {
        this.value = value;
    }

    public static HttpMime find(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new CustomNotFoundException("일치하는 MIME 타입을 찾을 수 없음"));
    }

    public String getValue() {
        return value;
    }
}
