package org.apache.coyote.http11.request;

import java.util.Arrays;
import nextstep.jwp.exception.HttpMethodNotAllowedException;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    ;

    private String text;

    HttpMethod(final String text) {
        this.text = text;
    }

    public static HttpMethod from(final String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(httpMethod -> httpMethod.text.equals(method))
                .findFirst()
                .orElseThrow(HttpMethodNotAllowedException::new);
    }
}
