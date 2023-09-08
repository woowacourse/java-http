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

    public static Builder ok() {
        return new Builder().httpStatus(HttpStatus.OK);
    }

    public static Builder found() {
        return new Builder().httpStatus(HttpStatus.FOUND);
    }

    public static Builder unauthorized() {
        return new Builder().httpStatus(HttpStatus.UNAUTHORIZED);
    }

    public static Builder notFound() {
        return new Builder().httpStatus(HttpStatus.NOT_FOUND);
    }

    public static Builder internalServerError() {
        return new Builder().httpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static ResponseEntity from(Builder builder) {
        HttpStatus httpStatus = getDefaultIfNullHttpStatus(builder.httpStatus);
        String path = builder.path;
        HttpCookie httpCookie = getDefaultIfNullHttpCookie(builder.httpCookie);

        return new ResponseEntity(httpStatus, path, httpCookie);
    }

    private static HttpStatus getDefaultIfNullHttpStatus(HttpStatus httpStatus) {
        if (httpStatus == null) {
            return HttpStatus.OK;
        }
        return httpStatus;
    }

    private static HttpCookie getDefaultIfNullHttpCookie(HttpCookie httpCookie) {
        if (httpCookie == null) {
            return HttpCookie.create();
        }
        return httpCookie;
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

    public static class Builder {
        private HttpStatus httpStatus;
        private String path;
        private HttpCookie httpCookie;

        private Builder() {
        }

        private Builder httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder httpCookie(HttpCookie httpCookie) {
            this.httpCookie = httpCookie;
            return this;
        }

        public ResponseEntity build() {
            return ResponseEntity.from(this);
        }
    }

}
