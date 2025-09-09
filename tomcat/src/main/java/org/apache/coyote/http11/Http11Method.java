package org.apache.coyote.http11;

import java.util.Arrays;

public enum Http11Method {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),;

    private final String method;

    Http11Method(String method) {
        this.method = method;
    }

    public static Http11Method from(String method) throws Http11ParseException{
        return Arrays.stream(Http11Method.values())
                .filter(m -> m.name().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(() -> new Http11ParseException(ParseError.INVALID_HTTP_METHOD));
    }

    public String getMethod() {
        return method;
    }
}
