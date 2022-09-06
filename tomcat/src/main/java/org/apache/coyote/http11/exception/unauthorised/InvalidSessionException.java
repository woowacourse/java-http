package org.apache.coyote.http11.exception.unauthorised;

public class InvalidSessionException extends UnAuthorisedException {

    public InvalidSessionException() {
        super("해당 세션이 존재하지 않습니다.");
    }
}
