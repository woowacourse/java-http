package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST
    ;

    public static HttpMethod from(String input) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(input))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 HTTP METHOD 는 존재하지 않습니다."));
    }

}
