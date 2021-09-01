package nextstep.jwp.webserver;

public class MethodNotAllowedException extends BaseException {

    public MethodNotAllowedException() {
        super(405);
    }
}
