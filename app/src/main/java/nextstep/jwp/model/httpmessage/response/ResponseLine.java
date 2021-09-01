package nextstep.jwp.model.httpmessage.response;


public class ResponseLine {

    private static final String PROTOCOL = "HTTP/1.1";
    private final HttpStatus httpStatus;

    public ResponseLine(HttpStatus status) {
        this.httpStatus = status;
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }

    public String getProtocol() {
        return PROTOCOL;
    }
}
