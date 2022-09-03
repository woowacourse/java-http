package org.apache.coyote.exception;

public class FileNotExistException extends RuntimeException {

    public FileNotExistException(String fileName) {
        super(fileName + " 파일이 존재하지 않습니다.");
    }
}
