package org.apache.coyote.http11.httpmessage.request;

import java.util.Arrays;

public enum Method {
    GET, POST, PUT, PATCH, DELETE;

    public static Method findByName(String name) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 Http 메서드 입니다."));
    }
}
