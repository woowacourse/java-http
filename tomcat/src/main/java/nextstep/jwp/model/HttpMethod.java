package nextstep.jwp.model;

import java.util.Arrays;
import java.util.Objects;
import nextstep.jwp.exception.NotFoundMethodException;

public enum HttpMethod {

    GET("get"),
    POST("post"),
    PUT("put"),
    PATCH("patch"),
    DELETE("delete"),
    OPTIONS("options"),
    ;

    private final String method;

    HttpMethod(final String method) {
        this.method = method;
    }

    public static HttpMethod from(String method) {
        Objects.requireNonNull(method);
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.isEqualsMethod(method))
                .findFirst()
                .orElseThrow(NotFoundMethodException::new);
    }

    private boolean isEqualsMethod(final String method) {
        return this.method.equalsIgnoreCase(method);
    }
}
