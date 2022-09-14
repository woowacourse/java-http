package org.apache.container.exception;

public class ContainerNotInitializedException extends RuntimeException {

    public ContainerNotInitializedException() {
        super("컨테이너가 올바르게 초기화되지 않았습니다.");
    }
}
