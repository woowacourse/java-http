package org.apache.coyote.http11.httpResponse;

import org.apache.coyote.http11.httpRequest.HttpCookie;

public record ResponseContent(
        HttpStatus httpStatus,
        String body,
        String location,
        HttpCookie httpCookie
) {

    public static ResponseContent success(
            final String body,
            final HttpCookie httpCookie
    ) {
        return new ResponseContent(HttpStatus.OK, body, null, httpCookie);
    }

    public static ResponseContent redirect(
            final String body,
            final String location,
            final HttpCookie httpCookie
    ) {
        return new ResponseContent(HttpStatus.FOUND, body, location, httpCookie);
    }

    public static ResponseContent error(final String body) {
        return new ResponseContent(HttpStatus.NOT_FOUND, body, null, null);
    }
}
