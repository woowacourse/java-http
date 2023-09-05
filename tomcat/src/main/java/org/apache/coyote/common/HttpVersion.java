package org.apache.coyote.common;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private final String source;

    HttpVersion(final String source) {
        this.source = source;
    }

    public static HttpVersion from(final String source) {
        return Arrays.stream(HttpVersion.values())
                .filter(httpVersion -> httpVersion.source.equals(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("입력된 값으로 HttpVersion을 조회할 수 없습니다."));
    }

    public String version() {
        return source;
    }

    @Override
    public String toString() {
        return "HttpVersion{" +
               "source='" + source + '\'' +
               '}';
    }
}
