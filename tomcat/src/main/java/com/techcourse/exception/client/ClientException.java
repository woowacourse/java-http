package com.techcourse.exception.client;

import com.techcourse.exception.UncheckedServletException;

public class ClientException extends UncheckedServletException {

    public ClientException(String message) {
        super(message);
    }
}
