package org.apache.coyote.http11;

import java.util.Arrays;

public enum HeaderType {

    COOKIE("Cookie"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    ;

    private final String name;

    HeaderType(String name) {
        this.name = name;
    }

    public static HeaderType findByName(String name) {
        return Arrays.stream(values())
                .filter(headerType -> headerType.name.equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("헤더 타입을 찾을 수 없습니다. : " + name));
    }
}
