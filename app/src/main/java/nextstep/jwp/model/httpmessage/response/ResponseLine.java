package nextstep.jwp.model.httpmessage.response;


public class ResponseLine {

    public static final String PROTOCOL = "HTTP/1.1";
    private final HttpStatus httpStatus;

    public ResponseLine(HttpStatus status) {
        this.httpStatus = status;
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String toString() {
        return PROTOCOL + " " + httpStatus + " ";
    }
}
