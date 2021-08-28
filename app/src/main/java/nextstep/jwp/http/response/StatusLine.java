package nextstep.jwp.http.response;

public class StatusLine {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private final ResponseStatus responseStatus;

    public StatusLine(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    @Override
    public String toString() {
        return String.join(" ",
                PROTOCOL_VERSION, responseStatus.getCodeAsString(), responseStatus.getMessage()) + " ";
    }
}
