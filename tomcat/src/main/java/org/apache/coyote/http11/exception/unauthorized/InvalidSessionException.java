package org.apache.coyote.http11.exception.unauthorized;

public class InvalidSessionException extends UnAuthorizedException {

    public InvalidSessionException() {
        super("해당 세션이 존재하지 않습니다.");
    }
}
