package nextstep.jwp.framework.http;

import static nextstep.jwp.framework.http.HttpRequest.DELIMITER;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS");

    private static final int HTTP_REQUEST_INDEX = 0;

    private final String method;

    HttpMethod(final String method) {
        this.method = method;
    }

    public static HttpMethod findRequest(final String line) {
        return findByString(line.split(DELIMITER)[HTTP_REQUEST_INDEX]);
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
