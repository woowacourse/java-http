package nextstep.jwp.web.http.response;

public class StatusLine {

    public static final String DEFAULT_PROTOCOL_VERSION = "HTTP/1.1";

    private String protocolVersion;

    private StatusCode statusCode;

    public StatusLine() {
        this.protocolVersion = "";
        this.statusCode = null;
    }

    public StatusLine(StatusCode statusCode) {
        this.protocolVersion = DEFAULT_PROTOCOL_VERSION;
        this.statusCode = statusCode;
    }

    public String asString() {
        return protocolVersion + " " + statusCode.getCode() + " " + statusCode
            .getStatusText();
    }
}
