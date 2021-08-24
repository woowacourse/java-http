package nextstep.jwp.http;

import java.util.Arrays;

public enum HttpMethod {
    GET, POST, PUT;

    public static HttpMethod of(String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.name().equalsIgnoreCase(method))
                .findAny().orElseThrow(() -> new IllegalArgumentException("해당하는 HTTP 메서드를 찾을 수 없습니다."));
    }
}
