package com.spring.http.response;

import com.spring.http.enums.HttpStatus;
import com.spring.http.request.HttpRequest;

public final class ResponseStartLine {
    private String version;
    private HttpStatus httpStatus;

    public ResponseStartLine(String version) {
        this(version, HttpStatus.OK);
    }

    public ResponseStartLine(HttpRequest request) {
        this(request.requestStartLine().version(), HttpStatus.OK);
    }

    public ResponseStartLine(String version, HttpStatus httpStatus) {
        this.version = version;
        this.httpStatus = httpStatus;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return version + " " + httpStatus.getCode() + " " + httpStatus.getReasonPhrase();
    }
}
