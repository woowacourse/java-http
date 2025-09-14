package org.apache.catalina.exception;

import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatus;

public class PathNotFoundException extends Http4xxException {

    public PathNotFoundException(Http11Response response) {
        super("No handler found for the request", response, HttpStatus.NOT_FOUND);
    }
}
