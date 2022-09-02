package org.apache.coyote.http11.utill;

public class InvalidFileNameException extends RuntimeException {

    public InvalidFileNameException(final String message) {
        super(message);
    }

    public InvalidFileNameException() {
        this("올바르지 않은 file name 형식입니다.");
    }
}
