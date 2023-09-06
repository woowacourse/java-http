package org.apache.coyote.http.response.exception;

public class UnsupportedAcceptException extends IllegalArgumentException {

    public UnsupportedAcceptException() {
        super("해당 Accept에 맞는 리소스를 찾을 수 없습니다.");
    }
}
