package org.apache.coyote.http11.http.common.startline;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1"),
    ;

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public static HttpVersion find(String target) {
        return Arrays.stream(HttpVersion.values())
                .filter(httpVersion -> httpVersion.value.equals(target))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 http version입니다 %s".formatted(target)));
    }

    public String getValue() {
        return value;
    }
}
