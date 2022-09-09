package org.apache.container.exception;

public class ContainerNotInitializedException extends RuntimeException {

    public ContainerNotInitializedException() {
        super("기본 요청 핸들러가 지정되지 않았습니다.");
    }
}
