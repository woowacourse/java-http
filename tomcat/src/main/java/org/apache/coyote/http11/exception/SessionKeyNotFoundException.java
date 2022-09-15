package org.apache.coyote.http11.exception;

public class SessionKeyNotFoundException extends RuntimeException {

    public SessionKeyNotFoundException(final String value) {
        super(String.format("존재하지 않는 세션 Attribute 입니다.(%s)", value));
    }
}
