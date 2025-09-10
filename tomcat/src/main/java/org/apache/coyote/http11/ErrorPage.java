package org.apache.coyote.http11;

import org.apache.coyote.http11.exception.CommonException;

public enum ErrorPage {

    UNAUTHORIZED("401.html", 401),
    NOT_FOUND("404.html", 404),
    ;

    private final String fileName;
    private final int statusCode;

    ErrorPage(
            String fileName,
            int statusCode
    ) {
        this.fileName = fileName;
        this.statusCode = statusCode;
    }

    public static String getFileName(HttpStatus httpStatus) {
        for (ErrorPage errorPage : values()) {
            if (httpStatus.getStatusCode() == errorPage.statusCode) {
                return errorPage.fileName;
            }
        }
        throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
