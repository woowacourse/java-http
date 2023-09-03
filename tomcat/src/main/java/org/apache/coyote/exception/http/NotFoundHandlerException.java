package org.apache.coyote.exception.http;

public class NotFoundHandlerException extends HttpException {

    public NotFoundHandlerException() {
        super("Match Handler Not Found");
    }
}
