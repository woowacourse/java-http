package nextstep.jwp;

public class StatusLine {

    private final String protocolVersion = "HTTP/1.1";
    private final HttpStatus httpStatus;

    public StatusLine(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String asString() {
        return protocolVersion + " " + httpStatus.toCode() + " " + httpStatus.toReasonPhrase() + " ";
    }
}
