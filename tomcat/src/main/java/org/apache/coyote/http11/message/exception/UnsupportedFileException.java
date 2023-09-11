package org.apache.coyote.http11.message.exception;

public class UnsupportedFileException extends RuntimeException {

    public UnsupportedFileException() {
        super("지원하지 않는 파일형식입니다.");
    }
}
