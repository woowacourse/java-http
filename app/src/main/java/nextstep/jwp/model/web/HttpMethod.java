package nextstep.jwp.model.web;

import java.util.Arrays;

public enum HttpMethod {
    GET, POST, PUT, DELETE, OPTION;

    public static HttpMethod findMethod(String method) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(method))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Method를 찾지 못했습니다."));
    }
}
