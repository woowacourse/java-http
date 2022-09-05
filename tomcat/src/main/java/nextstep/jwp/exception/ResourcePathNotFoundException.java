package nextstep.jwp.exception;

public class ResourcePathNotFoundException extends NotFoundException{

    public ResourcePathNotFoundException() {
        super("존재하지 않는 리소스 경로입니다.");
    }
}
