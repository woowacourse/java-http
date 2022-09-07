package org.apache.coyote.http11.exception;

public class ElementNotFoundException extends RuntimeException {

    public ElementNotFoundException() {
        super("존재하지 않는 데이터입니다.");
    }
}
