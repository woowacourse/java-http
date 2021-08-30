package nextstep.jwp.http;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE");

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static HttpMethod findHttpMethod(String httpMethodName) {
        return Arrays.stream(HttpMethod.values())
            .filter(method -> method.name.equals(httpMethodName))
            .findFirst()
            .orElseThrow(() -> new CustomException("존재하지 않는 HttpMethod입니다."));
    }
}
