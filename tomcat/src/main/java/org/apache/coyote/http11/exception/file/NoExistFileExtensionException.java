package org.apache.coyote.http11.exception.file;

public class NoExistFileExtensionException extends RuntimeException {

    public NoExistFileExtensionException() {
        super("파일 확장자가 존재하지 않습니다.");
    }
}
