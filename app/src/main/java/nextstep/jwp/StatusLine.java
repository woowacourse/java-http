package nextstep.jwp;

public class StatusLine {

    private final String protocolVersion = "HTTP/1.1";
    private final HttpStatus httpStatus = HttpStatus.OK;

    public StatusLine() {
    }

    public String asString() {
        return protocolVersion + " " + httpStatus.toCode() + " " + httpStatus.toReasonPhrase() + " ";
    }
}
