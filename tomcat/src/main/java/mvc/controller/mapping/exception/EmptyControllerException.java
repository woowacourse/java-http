package mvc.controller.mapping.exception;

public class EmptyControllerException extends IllegalArgumentException {

    public EmptyControllerException() {
        super("등록된 Controller가 존재하지 않습니다.");
    }
}
