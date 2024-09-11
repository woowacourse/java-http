package org.apache.catalina.controller.http;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion of(String version) {
        return Arrays.stream(values())
                .filter(v -> v.getVersion().equals(version))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 버전(%s)이 존재하지 않습니다.".formatted(version)));
    }

    public String getVersion() {
        return version;
    }
}
