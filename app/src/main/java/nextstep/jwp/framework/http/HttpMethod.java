package nextstep.jwp.framework.http;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS");

    private final String method;

    HttpMethod(final String method) {
        this.method = method;
    }

    public static HttpMethod findByString(final String request) {
        return Arrays.stream(HttpMethod.values())
            .filter(httpMethod -> httpMethod.getMethod().equals(request))
            .findAny()
            .orElseThrow(UnsupportedOperationException::new);
    }

    public String getMethod() {
        return method;
    }

    public boolean isPost() {
        return this.method.equals(POST.method);
    }

    public boolean isGet() {
        return this.method.equals(GET.method);
    }
}
