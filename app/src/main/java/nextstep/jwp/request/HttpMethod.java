package nextstep.jwp.request;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST");

    private final String httpMethod;

    HttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public static HttpMethod of(String input) {
        return Arrays.stream(values())
                .filter(method -> method.httpMethod.equalsIgnoreCase(input))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP Method 입니다."));
    }
}
