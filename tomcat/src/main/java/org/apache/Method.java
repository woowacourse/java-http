package org.apache;

import java.util.Arrays;

public enum Method {

    OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT;

    public static Method from(String method) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(method))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메서드입니다."));
    }
}
