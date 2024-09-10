package org.was.Controller.exception;

public class ControllerNotFoundException extends RuntimeException{

    public ControllerNotFoundException(String path) {
        super("요청 경로 처리를 위한 컨트롤러가 존재하지 않습니다. :" + path);
    }
}
