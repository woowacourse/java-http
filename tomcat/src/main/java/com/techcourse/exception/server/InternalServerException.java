package com.techcourse.exception.server;

import com.techcourse.exception.UncheckedServletException;

public class InternalServerException extends UncheckedServletException {

    public InternalServerException(String message) {
        super(message);
    }
}
