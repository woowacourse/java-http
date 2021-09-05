package nextstep.jwp.framework.http.common;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS");

    private final String name;

    HttpMethod(final String name) {
        this.name = name;
    }

    public static HttpMethod findByString(final String request) {
        return Arrays.stream(HttpMethod.values())
            .filter(httpMethod -> httpMethod.getName().equals(request))
            .findAny()
            .orElseThrow(UnsupportedOperationException::new);
    }

    public boolean isPost() {
        return this.name.equals(POST.name);
    }

    public boolean isGet() {
        return this.name.equals(GET.name);
    }


    public String getName() {
        return name;
    }
}
