package nextstep.jwp.framework;

import static nextstep.jwp.framework.RequestHeader.DELIMITER;

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

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod findRequest(String line) {
        return findByString(line.split(DELIMITER)[HTTP_REQUEST_INDEX]);
    }

    public static HttpMethod findByString(String request) {
        return Arrays.stream(HttpMethod.values())
            .filter(httpMethod -> httpMethod.getMethod().equals(request))
            .findAny()
            .orElseThrow(UnsupportedOperationException::new);
    }

    public String getMethod() {
        return method;
    }
}
