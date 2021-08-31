package nextstep.jwp.handler;

public enum HttpMethod {

    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    CONNECT("CONNECT"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    PATCH("PATCH");

    private final String methodName;

    HttpMethod(String methodName) {
        this.methodName = methodName;
    }

    public static HttpMethod from(String methodName) {
        for (HttpMethod httpMethod : HttpMethod.values()) {
            if (httpMethod.isSameAs(methodName)) {
                return httpMethod;
            }
        }

        throw new IllegalArgumentException("적절한 Http 메소드가 존재하지 않습니다.");
    }

    public boolean isSameAs(String methodName) {
        return this.methodName.equals(methodName);
    }
}
