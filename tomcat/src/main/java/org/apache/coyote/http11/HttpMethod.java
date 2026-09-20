package org.apache.coyote.http11;

import java.io.IOException;

public enum HttpMethod {
    // HTTP Method는 대소문자 구분을 하지 않으므로, 완전히 같아야 한다.
    GET,
    POST,
    PUT,
    DELETE;


    public static HttpMethod toValue(String value) throws IOException {
        for (HttpMethod method : values()) {
            if (method.name().equals(value)) {
                return method;
            }
        }
        throw new IOException("지원하지 않는 HTTP Method 입니다.");
    }
}
