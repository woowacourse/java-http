package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {
    POST,
    GET,
    ;

    public static HttpMethod from(String method) {
        return Arrays.stream(HttpMethod.values())
                .filter(value -> method.equals(value.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 HTTP METHOD 요쳥: " + method));
    }

    public boolean isPost(HttpMethod httpMethod) {
        return httpMethod.equals(POST);
    }

    public boolean isGet(HttpMethod httpMethod) {
        return httpMethod.equals(GET);
    }

    public boolean isValidMethod(HttpMethod httpMethod) {
        return Arrays.asList(values()).contains(httpMethod);
    }
}
