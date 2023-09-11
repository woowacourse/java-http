package org.apache.coyote.http11.exception.file;

public class UnsupportedFileException extends RuntimeException {

    public UnsupportedFileException() {
        super("지원하지 않는 파일형식입니다.");
    }
}
