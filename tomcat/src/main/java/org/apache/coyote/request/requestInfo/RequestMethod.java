package org.apache.coyote.request.requestInfo;

import java.util.Arrays;

public enum RequestMethod {

    POST,
    GET,
    PUT,
    PATCH,
    DELETE;

    public static RequestMethod from(final String method){
        return Arrays.stream(RequestMethod.values())
                .filter(requestMethod -> requestMethod.name().equals(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 메서드입니다"));
    }
}
