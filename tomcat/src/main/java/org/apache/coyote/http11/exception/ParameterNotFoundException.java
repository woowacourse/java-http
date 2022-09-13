package org.apache.coyote.http11.exception;

import java.util.NoSuchElementException;

public class ParameterNotFoundException extends NoSuchElementException {

    public ParameterNotFoundException(final String parameter) {
        super("파라미터를 찾을 수 없습니다 : " + parameter);
    }
}
