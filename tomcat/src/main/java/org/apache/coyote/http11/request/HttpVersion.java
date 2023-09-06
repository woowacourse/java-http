package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.exception.NotFoundHttpVersionException;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public static HttpVersion findBy(final String version) {
        return Arrays.stream(values())
                     .filter(value -> value.value.equals(version))
                     .findFirst().orElseThrow(() -> new NotFoundHttpVersionException("해당 HTTP 버전을 찾을 수 없습니다."));
    }
}
