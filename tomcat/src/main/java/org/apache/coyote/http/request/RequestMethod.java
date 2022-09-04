package org.apache.coyote.http.request;

import java.util.Arrays;

public enum RequestMethod {

    GET,
    POST,
    ;

    public static RequestMethod findMethod(String string) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(string))
                .findAny()
                .orElseThrow(()-> new IllegalArgumentException("해당되는 메소드 유형이 존재하지 않습니다."));
    }
}
