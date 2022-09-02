package org.apache.coyote.http11.exception;

public class ReadFileException extends RuntimeException {

    public ReadFileException() {
        super("파일을 읽는데 오류가 발생하였습니다.");
    }
}
