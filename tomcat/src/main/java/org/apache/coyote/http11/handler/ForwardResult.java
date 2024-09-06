package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpStatus;

import java.util.Collections;

record ForwardResult(String path, HttpStatus httpStatus, Header header) {

    ForwardResult(HttpStatus httpStatus, Header header) {
        this("", httpStatus, header);
    }

    ForwardResult(String path, HttpStatus httpStatus) {
        this(path, httpStatus, new Header(Collections.emptyList()));
    }
}
