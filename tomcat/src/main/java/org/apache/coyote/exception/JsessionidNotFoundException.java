package org.apache.coyote.exception;

public class JsessionidNotFoundException extends InternalServerException {

    public JsessionidNotFoundException() {
        super("JSESSIONID를 찾을 수 없습니다.");
    }
}
