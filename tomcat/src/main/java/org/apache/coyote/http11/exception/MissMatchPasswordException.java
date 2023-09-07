package org.apache.coyote.http11.exception;

public class MissMatchPasswordException extends RuntimeException{

    public MissMatchPasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
