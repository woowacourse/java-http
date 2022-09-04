package org.apache.coyote.http11.exception;

public class StaticFileNotFoundException extends RuntimeException {

    public StaticFileNotFoundException(final String path) {
        super(path + "에 파일이 존재하지 않습니다.");
    }
}
