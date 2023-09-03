package nextstep.jwp.http;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    ;

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    // TODO: 2023/09/04 예외 클래스 변경
    public static HttpMethod from(String input) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(input))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String getValue() {
        return value;
    }
}
