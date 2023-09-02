package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;

public class HttpRequest {

    private final HttpMethodType httpMethodType;
    private final HttpPath httpPath;

    private HttpRequest(final HttpMethodType httpMethodType, final HttpPath httpPath) {
        this.httpMethodType = httpMethodType;
        this.httpPath = httpPath;
    }

    public static HttpRequest from(final String request) {
        if (request == null) {
            throw new UncheckedServletException("request가 존재하지 않습니다.");
        }

        final String[] split = request.split(" ");
        return new HttpRequest(HttpMethodType.from(split[0]), HttpPath.from(split[1]));
    }

    public HttpMethodType getHttpMethodType() {
        return httpMethodType;
    }

    public HttpPath getHttpPath() {
        return httpPath;
    }
}
