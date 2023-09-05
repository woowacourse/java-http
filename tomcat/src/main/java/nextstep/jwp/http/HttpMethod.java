package nextstep.jwp.http;

import java.util.Arrays;
import nextstep.jwp.exception.InvalidRequestMethod;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    ;

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod from(String input) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(input))
                .findAny()
                .orElseThrow(InvalidRequestMethod::new);
    }

    public String getValue() {
        return value;
    }
}
