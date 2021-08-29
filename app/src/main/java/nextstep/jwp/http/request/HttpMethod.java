package nextstep.jwp.http.request;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod of(String methodName) {
        return Arrays.stream(values())
            .filter(httpMethod -> httpMethod.getMethod().equals(methodName))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 http 요청입니다."));
    }

    private String getMethod() {
        return method;
    }

    public boolean isGet() {
        return "GET".equals(method);
    }

    public boolean isPost() {
        return "POST".equals(method);
    }
}
