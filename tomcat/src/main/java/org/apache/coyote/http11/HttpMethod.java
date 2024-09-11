package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.exception.NotFoundException;

public enum HttpMethod {

    GET,
    POST
    ;

    public static HttpMethod getHttpMethod(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(name))
                .findAny()
                .orElseThrow(() -> new NotFoundException("유효하지 않은 메소드 입니다."));
    }

    public boolean isMethod(HttpMethod method) {
        return this.equals(method);
    }
}
