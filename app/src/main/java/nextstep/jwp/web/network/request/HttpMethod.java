package nextstep.jwp.web.network.request;

import nextstep.jwp.web.exception.InvalidHttpMethodException;

import java.util.Arrays;

public enum HttpMethod {

    OPTIONS,
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    CONNECT;

    public static HttpMethod of(String name) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.is(name))
                .findFirst()
                .orElseThrow(() -> new InvalidHttpMethodException(name));
    }

    private boolean is(String name) {
        return this.name().equals(name);
    }

    public boolean isGet() {
        return this.equals(GET);
    }

    public boolean isPost() {
        return this.equals(POST);
    }
}
