package nextstep.jwp.framework.http.request.details;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST");

    private final String method;

    HttpMethod(final String method) {
        this.method = method;
    }

    public static HttpMethod of(final String input) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.method.equalsIgnoreCase(input))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP Method 입니다."));
    }
}
