package nextstep.jwp.http.exception;

public class StaticFileNotFoundException extends HttpException {
    public StaticFileNotFoundException(String message) {
        super(message);
    }
}
