package org.apache.coyote.http11;

import java.util.Collections;

public record ForwardResult(String path, HttpStatus httpStatus, Header header) {

    public ForwardResult(HttpStatus httpStatus, Header header) {
        this("", httpStatus, header);
    }

    public ForwardResult(String path, HttpStatus httpStatus) {
        this(path, httpStatus, new Header(Collections.emptyList()));
    }
}
