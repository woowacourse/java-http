package org.apache.coyote.http11.response.header;

import java.util.Arrays;

import org.apache.coyote.http11.common.HttpMessageDelimiter;

public enum StatusCode {

    OK(200, "OK"),

    FOUND(302, "Found"),

    UNAUTHORIZED(401, "Unauthorized"),
    ;

    private final int code;
    private final String name;

    StatusCode(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    public static StatusCode findBy(final String statusCode) {
        return Arrays.stream(values())
            .filter(value -> existCode(statusCode, value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("존재하지 않는 상태코드 입니다. [%s]", statusCode)));
    }

    private static boolean existCode(final String statusCode, final StatusCode existCode) {
        return String.valueOf(existCode.code).equals(statusCode) || existCode.name.equals(statusCode);
    }

    public String toMessage() {
        return String.join(HttpMessageDelimiter.WORD.getValue(), String.valueOf(code), name);
    }
}
