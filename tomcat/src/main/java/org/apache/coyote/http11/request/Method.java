package org.apache.coyote.http11.request;

import java.util.Arrays;
import org.apache.coyote.exception.InvalidMethodException;

public enum Method {

    GET,
    POST,
    ;

    public static Method from(String name) {
        return Arrays.stream(values())
                .filter(method -> name.equals(method.name()))
                .findFirst()
                .orElseThrow(InvalidMethodException::new);
    }
}
