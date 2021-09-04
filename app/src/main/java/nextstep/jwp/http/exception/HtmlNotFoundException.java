package nextstep.jwp.http.exception;

public class HtmlNotFoundException extends StaticResourceNotFoundException {
    public HtmlNotFoundException(String message) {
        super(message);
    }
}
