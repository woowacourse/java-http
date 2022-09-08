package org.apache.catalina.exception;

public class JsessionidNotFoundException extends InternalServerException {

    public JsessionidNotFoundException() {
        super("JSESSIONID를 찾을 수 없습니다.");
    }
}
