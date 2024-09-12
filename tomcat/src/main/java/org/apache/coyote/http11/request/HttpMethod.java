package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    ;

    public static HttpMethod findByName(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("입력 값과 일치하는 HttpMethod가 없습니다. 입력 = " + name));
    }

    public boolean isPost() {
        return this == HttpMethod.POST;
    }

    public boolean isGet() {
        return this == HttpMethod.GET;
    }
}
