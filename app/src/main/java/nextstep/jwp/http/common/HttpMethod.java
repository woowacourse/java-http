package nextstep.jwp.http.common;

import java.util.Objects;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public boolean isSame(String other) {
        return Objects.equals(method, other);
    }
}
