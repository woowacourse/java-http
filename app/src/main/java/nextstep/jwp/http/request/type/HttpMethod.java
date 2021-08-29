package nextstep.jwp.http.request.type;

import java.util.Arrays;

public enum HttpMethod {
    GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, PATCH;

    public static HttpMethod of(String method) throws IllegalStateException {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.name().equalsIgnoreCase(method))
                .findAny().orElseThrow(() -> new IllegalStateException("해당하는 HTTP 메서드를 찾을 수 없습니다."));
    }

    public boolean isGet() {
        return this == GET;
    }

    public boolean isPost() {
        return this == POST;
    }
}
