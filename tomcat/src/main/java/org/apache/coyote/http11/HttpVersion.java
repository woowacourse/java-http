package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpVersion {

    HTTP10("HTTP/1.0"),
    HTTP11("HTTP/1.1"),
    HTTP20("HTTP/2.0"),
    HTTP30("HTTP/3.0"),
    ;

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public static HttpVersion findVersion(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.value.equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 http버전이 존재하지 않습니다."));
    }

    public String getValue() {
        return value;
    }
}
