package org.apache.coyote.http11;

import java.io.IOException;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE;

    public static HttpMethod from(String value) throws IOException {
        for (HttpMethod method : values()) {
            if (method.name().equals(value)) {
                return method;
            }
        }
        throw new IOException("지원하지 않는 HTTP Method 입니다.");
    }
}
