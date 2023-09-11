package org.apache.coyote.http11.common;

import java.util.Arrays;
import org.apache.coyote.http11.exception.BadRequestException;

public enum HttpVersion {
    V1_0("HTTP/1.0"),
    V1_1("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion from(String input) {
        return Arrays.stream(values())
                .filter(value -> value.getValue().equals(input))
                .findAny()
                .orElseThrow(() -> new BadRequestException("지원하지 않는 HTTP 버전입니다."));
    }

    public String getValue() {
        return value;
    }

}
