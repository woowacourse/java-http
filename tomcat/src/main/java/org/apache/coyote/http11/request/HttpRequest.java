package org.apache.coyote.http11.request;

import nextstep.jwp.exception.UncheckedServletException;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final Path path;

    private HttpRequest(HttpMethod httpMethod, Path path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public static HttpRequest from(String request) {
        if (request == null) {
            throw new UncheckedServletException("request가 null이어서는 안됩니다.");
        }

        final String[] split = request.split(" ");
        return new HttpRequest(HttpMethod.from(split[0]), Path.from(split[1]));
    }

    public Path getPath() {
        return path;
    }
}
