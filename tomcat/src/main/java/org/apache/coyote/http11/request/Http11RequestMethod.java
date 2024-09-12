package org.apache.coyote.http11.request;

import org.apache.coyote.http11.Http11Exception;
import java.util.Arrays;

public enum Http11RequestMethod {
    OPTIONS,GET,HEAD,POST,PUT,DELETE,TRACE,CONNECT;

    public static Http11RequestMethod from(String name) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new Http11Exception("Not Http11RequestMethod : " + name));
    }
}
