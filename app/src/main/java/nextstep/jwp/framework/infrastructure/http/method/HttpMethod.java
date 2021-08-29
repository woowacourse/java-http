package nextstep.jwp.framework.infrastructure.http.method;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private final String signature;

    HttpMethod(String signature) {
        this.signature = signature;
    }
}
