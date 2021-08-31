package nextstep.jwp.handler.constant;

import nextstep.jwp.exception.handler.HttpMessageException;

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

        throw new HttpMessageException("존재하지 않는 HTTP Method 입니다.");
    }

    public boolean isSameAs(String methodName) {
        return this.methodName.equals(methodName);
    }

    public String getMethodName() {
        return methodName;
    }
}
