package org.apache.coyote.http11;

import org.apache.coyote.http11.response.HttpStatusCode;

public class HttpException extends Exception {

    public HttpException(HttpStatusCode statusCode) {
        super(statusCode.getStatus());
    }
}
