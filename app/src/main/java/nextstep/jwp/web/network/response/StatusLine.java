package nextstep.jwp.web.network.response;

public class StatusLine {

    private static final String STATUS_LINE_FORMAT = "%s %s %s ";

    private final String protocolVersion;
    private HttpStatus httpStatus;

    public StatusLine(HttpStatus httpStatus) {
        this.protocolVersion = "HTTP/1.1";
        this.httpStatus = httpStatus;
    }

    public String print() {
        return String.format(STATUS_LINE_FORMAT, protocolVersion, httpStatus.toCode(), httpStatus.toReasonPhrase());
    }

    public void setStatus(HttpStatus status) {
        this.httpStatus = status;
    }
}
