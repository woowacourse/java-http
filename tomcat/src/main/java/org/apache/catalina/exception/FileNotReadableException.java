package org.apache.catalina.exception;

public class FileNotReadableException extends RuntimeException {

    public FileNotReadableException() {
        super("파일을 읽는데 실패했습니다.");
    }
}
