package nextstep.jwp.http.exception;

public class HtmlNotFoundException extends StaticFileNotFoundException {
    public HtmlNotFoundException(String message) {
        super(message);
    }
}
