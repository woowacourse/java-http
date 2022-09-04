package org.apache.coyote.http11.handler.FileHandler;

import org.apache.coyote.http11.httpmessage.response.HttpStatus;

public class FileHandlerResponse {

    private final HttpStatus httpStatus;
    private final String path;

    public FileHandlerResponse(HttpStatus httpStatus, String path) {
        this.httpStatus = httpStatus;
        this.path = path;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getPath() {
        return path;
    }
}
