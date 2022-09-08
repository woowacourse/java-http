package org.apache.coyote.http11.exception;

public class FileIOException extends RuntimeException{

    private static final String MESSAGE = "파일을 읽어오는데 문제가 발생했습니다.";

    public FileIOException() {
        super(MESSAGE);
    }
}
