package org.apache.coyote;

public class UnauthorizedException extends RuntimeException{

    public UnauthorizedException() {
        super("회원을 찾을 수 없습니다.");
    }
}
