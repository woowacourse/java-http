package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpStatus;

public class ResponseEntity {

    private final HttpStatus httpStatus;
    private final String path;
    private final HttpCookie httpCookie;

    private ResponseEntity(HttpStatus httpStatus, String path, HttpCookie httpCookie) {
        this.httpStatus = httpStatus;
        this.path = path;
        this.httpCookie = httpCookie;
    }

    public static ResponseEntity of(HttpStatus httpStatus, String path) {
        return new ResponseEntity(httpStatus, path, HttpCookie.create());
    }

    public static ResponseEntity cookie(HttpCookie httpCookie, HttpStatus httpStatus, String path) {
        return new ResponseEntity(httpStatus, path, httpCookie);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getPath() {
        return path;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

}
