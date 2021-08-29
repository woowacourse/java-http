package nextstep.joanne.http;

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

    public boolean sameWith(HttpMethod other) {
        return Objects.equals(this, other);
    }
}
