package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {

    GET;

    public static HttpMethod from(final String httpMethod) {
        return Arrays.stream(values())
                .filter(each -> each.name().equals(httpMethod.toUpperCase()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(httpMethod+ " HTTP Method 는 지원 안해용 ~"));
    }
}
