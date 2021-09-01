package nextstep.jwp.http;

import java.util.Arrays;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public static HttpMethod matchHttpMethod(final String requestMethod) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(requestMethod))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("옳지 않은 함수입니다."));
    }
}
