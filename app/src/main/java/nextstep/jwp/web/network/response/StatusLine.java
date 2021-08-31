package nextstep.jwp.web.network.response;

public class StatusLine {

    private final String protocolVersion;
    private HttpStatus httpStatus;

    public StatusLine(HttpStatus httpStatus) {
        this.protocolVersion = "HTTP/1.1";
        this.httpStatus = httpStatus;
    }

    public String asString() {
        return protocolVersion + " " + httpStatus.toCode() + " " + httpStatus.toReasonPhrase() + " ";
    }

    public void setStatus(HttpStatus status) {
        this.httpStatus = status;
    }
}
