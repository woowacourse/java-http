package org.apache.coyote.http11;

public record ForwardResult(String path, HttpStatus httpStatus, Header header) {

    public ForwardResult(HttpStatus httpStatus, Header header) {
        this("", httpStatus, header);
    }

    public ForwardResult(String path, HttpStatus httpStatus) {
        this(path, httpStatus, Header.empty());
    }
}
