package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequestURI;

public class HttpResponseHeader {

    private final HttpStatus httpStatus;

    private HttpResponseHeader(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static HttpResponseHeader from(final HttpRequestURI httpRequestURI) {
        final var uri = httpRequestURI.getUri();
        final var queryString = httpRequestURI.getRequestBody();

        if (uri.startsWith("/index") && !queryString.isEmpty()) {
            return new HttpResponseHeader(HttpStatus.FOUND);
        }

        if (uri.startsWith("/401")) {
            return new HttpResponseHeader(HttpStatus.UNAUTHORIZED);
        }

        return new HttpResponseHeader(HttpStatus.OK);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
