package nextstep.jwp.http.common;

import java.util.Arrays;
import nextstep.jwp.exception.InvalidRequestMethodException;

public enum HttpMethod {

    GET("GET"),
    POST("POST")
    ;

    private final String value;

    HttpMethod(final String value) {
        this.value = value;
    }

    public static HttpMethod find(final String method) {
        return Arrays.stream(HttpMethod.values())
            .filter(value -> value.name().equals(method))
            .findAny()
            .orElseThrow(InvalidRequestMethodException::new);
    }

    public String getValue() {
        return value;
    }

    public boolean isSameMethod(String httpMethod) {
        return this.value.equals(httpMethod);
    }
}
