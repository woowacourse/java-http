package org.apache.coyote.exception;

public class NotFoundServletException extends RuntimeException {

    public NotFoundServletException() {
        super("Match Servlet Not Found");
    }
}
