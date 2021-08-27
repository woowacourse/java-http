package nextstep.jwp.http.response;

public class StatusLine {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    private final int statusCode;
    private final StatusMessage statusMessage;

    public StatusLine(int statusCode) {
        this.statusCode = statusCode;
        this.statusMessage = StatusMessage.findByStatusCode(statusCode);
    }

    @Override
    public String toString() {
        return String.join(" ",
                PROTOCOL_VERSION, String.valueOf(statusCode), statusMessage.getValue()) + " ";
    }
}
