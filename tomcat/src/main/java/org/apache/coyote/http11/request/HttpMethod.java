package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    ;

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public static HttpMethod findByName(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("입력 값과 일치하는 HttpMethod가 없습니다. 입력 = " + name));
    }

    public String getName() {
        return name;
    }

    public boolean isPost() {
        return this == HttpMethod.POST;
    }

    public boolean isGet() {
        return this == HttpMethod.GET;
    }
}
