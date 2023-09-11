package org.apache.catalina.exception.file;

public class NoExistFileExtensionException extends RuntimeException {

    public NoExistFileExtensionException() {
        super("파일 확장자가 존재하지 않습니다.");
    }
}
