package nextstep.jwp.http;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST;

    public static HttpMethod from(String httpMethod) {
        return Arrays.stream(values())
            .filter(it -> it.name().equals(httpMethod))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 Http 메서드 입니다."));
    }
}
