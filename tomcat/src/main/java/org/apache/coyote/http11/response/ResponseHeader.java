package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.RequestURI;

public class ResponseHeader {

    private final HttpStatus httpStatus;

    private ResponseHeader(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static ResponseHeader from(final RequestURI requestURI) {
        final var uri = requestURI.getUri();
        final var queryString = requestURI.getRequestBody();

        if (uri.startsWith("/index") && !queryString.isEmpty()) {
            return new ResponseHeader(HttpStatus.FOUND);
        }

        if (uri.startsWith("/401")) {
            return new ResponseHeader(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseHeader(HttpStatus.OK);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
