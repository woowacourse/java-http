package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpStatus.BAD_REQUEST;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    ;

    private final String methodName;

    HttpMethod(String methodName) {
        this.methodName = methodName;
    }

    public static HttpMethod from(String methodName) {
        return Arrays.stream(HttpMethod.values())
                .filter(it -> it.methodName.equals(methodName))
                .findAny()
                .orElseThrow(() -> new HttpException(BAD_REQUEST, "존재하지 않는 메서드입니다"));
    }
}
