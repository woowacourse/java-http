package org.apache.coyote.http11;

import java.util.Arrays;

public enum Http11Method {
    GET,
    POST;

    public static Http11Method findByName(String name) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Http11Method 입니다."));
    }
}
