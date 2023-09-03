package org.apache.coyote.http11.common;

public class ResponseHeader {

    private final HttpStatus httpStatus;

    public ResponseHeader(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static ResponseHeader from(final RequestURI requestURI) {
        final var uri = requestURI.getUri();
        final var queryString = requestURI.getQueryString();

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
