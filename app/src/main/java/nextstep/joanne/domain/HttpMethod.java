package nextstep.joanne.domain;

import java.util.Objects;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public boolean sameWith(String other) {
        return Objects.equals(method, other);
    }
}
