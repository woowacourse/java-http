package nextstep.jwp.framework.infrastructure.http.method;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS");

    private final String signature;

    HttpMethod(String signature) {
        this.signature = signature;
    }
}
