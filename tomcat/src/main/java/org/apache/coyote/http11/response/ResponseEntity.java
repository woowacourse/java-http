package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatus;

public class ResponseEntity {

    private final HttpMethod httpMethod;
    private final HttpStatus httpStatus;
    private final String path;

    public ResponseEntity(HttpMethod httpMethod, HttpStatus httpStatus, String path) {
        this.httpMethod = httpMethod;
        this.httpStatus = httpStatus;
        this.path = path;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getPath() {
        return path;
    }

    public boolean hasSameHttpMethod(HttpMethod httpMethod) {
        return httpMethod.isEqualTo(this.httpMethod);
    }

}
