package org.apache.coyote.http11.exception;

public class FileNotFoundException extends IllegalArgumentException {
    private static final String MESSAGE = "존재하지 않는 자원입니다. 입력한 자원: ";

    public FileNotFoundException(final String fileName) {
        super(MESSAGE + fileName);
    }

    public FileNotFoundException(final String fileName, final Throwable cause) {
        super(MESSAGE + fileName, cause);
    }
}
