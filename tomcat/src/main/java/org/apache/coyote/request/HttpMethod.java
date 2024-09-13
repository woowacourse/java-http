package org.apache.coyote.request;

import java.util.Arrays;
import org.apache.coyote.exception.CoyoteException;

public enum HttpMethod {

    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH,
    ;

    public static HttpMethod from(String name) {
        return Arrays.stream(values())
                .filter(method -> name.equals(method.name()))
                .findFirst()
                .orElseThrow(() -> new CoyoteException("올바르지 않은 HTTP Method입니다."));
    }
}
