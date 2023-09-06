package org.apache.coyote.http11.request;

import org.apache.coyote.http11.exception.InvalidRequestException;

import java.util.Arrays;

public enum RequestMethod {
    GET,
    POST,
    PATCH,
    UPDATE,
    DELETE,
    ;

    RequestMethod() {
    }

    public static RequestMethod getRequestMethod(String value) {
        return Arrays.stream(RequestMethod.values())
                .filter(requestMethod -> requestMethod.name().equals(value))
                .findFirst()
                .orElseThrow(InvalidRequestException::new);
    }
}
