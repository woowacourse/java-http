package org.apache.coyote.http11.session;

public class InvalidateSessionException extends RuntimeException {

    public InvalidateSessionException() {
        super("세션이 만료되었습니다.");
    }
}
