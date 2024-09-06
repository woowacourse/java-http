package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpMethod {

    GET,
    POST,
    ;

    public static HttpMethod from(String value) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public boolean isGet() {
        return this.equals(GET);
    }

    public boolean isPost() {
        return this.equals(POST);
    }
}
