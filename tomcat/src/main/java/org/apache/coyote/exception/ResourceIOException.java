package org.apache.coyote.exception;

public class ResourceIOException extends RuntimeException {
    public ResourceIOException(Exception cause) {
        super("리소스 처리에 문제가 발생했습니다.",cause);
    }
}
