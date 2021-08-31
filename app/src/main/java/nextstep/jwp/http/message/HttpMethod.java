package nextstep.jwp.http.message;

import java.util.Arrays;

public enum HttpMethod {
    GET("get"),
    HEAD("head"),
    POST("post"),
    PUT("put"),
    DELETE("delete"),
    OPTIONS("options"),
    PATCH("patch");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod of(String name) {
        return Arrays.stream(values())
            .filter(httpMethod -> httpMethod.value.equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("존재하지 않는 Http Method 입니다."));
    }
}
