package org.apache.coyote.http11.httprequest;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    ;

    public static HttpMethod from(String httpMethod) {
        return Arrays.stream(values())
                .filter(method -> method.isMethodMatch(httpMethod))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 HTTP METHOD를 찾을 수 없습니다. 입력된 HTTP METHOD: " + httpMethod));
    }

    private boolean isMethodMatch(String httpMethod) {
        return this.name().equals(httpMethod);
    }

    public boolean isGet() {
        return this == GET;
    }

    public boolean isPost() {
        return this == POST;
    }
}
