package org.apache.coyote.http11;

import org.apache.coyote.http11.exception.CommonException;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "NOT FOUND"),
    INTERNAL_SERVER_ERROR(500, "Interval Server Error"),
    ;

    private final int statusCode;
    private final String reasonPhrase;

    HttpStatus(
            int statusCode,
            String reasonPhrase
    ) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public static HttpStatus findByStatusCode(int statusCode) {
        for (HttpStatus httpStatus : values()) {
            if (httpStatus.getStatusCode() == statusCode) {
                return httpStatus;
            }
        }
        throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
