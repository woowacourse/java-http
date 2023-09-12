package org.apache.catalina.session;

public class SessionException extends RuntimeException {

    private static final String INVALID_SESSION_KEY_MESSAGE = "요청한 세션을 찾을 수 없습니다.";

    public SessionException() {
        super(INVALID_SESSION_KEY_MESSAGE);
    }
}
