package org.apache.coyote.http11.httpResponse;

import org.apache.coyote.http11.HttpStatus;

public record ResponseContent(
        HttpStatus httpStatus,
        String body,
        String location
) {

    public static ResponseContent success(
            final String body
    ) {
        return new ResponseContent(HttpStatus.OK, body, null);
    }

    public static ResponseContent redirect(
            final String body,
            final String location
    ) {
        return new ResponseContent(HttpStatus.FOUND, body, location);
    }

    public static ResponseContent error(
            final String body
    ) {
        return new ResponseContent(HttpStatus.NOT_FOUND, body, null);
    }
}
