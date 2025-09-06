package org.apache.coyote.http11.httpResponse;

import org.apache.coyote.http11.HttpStatus;

public record HttpResponseContent(
        HttpStatus httpStatus,
        String body,
        String location
) {

    public static HttpResponseContent success(
            final String body
    ) {
        return new HttpResponseContent(HttpStatus.OK, body, null);
    }

    public static HttpResponseContent redirect(
            final String body,
            final String location
    ) {
        return new HttpResponseContent(HttpStatus.FOUND, body, location);
    }

    public static HttpResponseContent error(
            final String body
    ) {
        return new HttpResponseContent(HttpStatus.NOT_FOUND, body, null);
    }
}
