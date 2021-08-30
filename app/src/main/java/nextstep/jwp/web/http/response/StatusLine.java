package nextstep.jwp.web.http.response;

public class StatusLine {

    public static final String DEFAULT_PROTOCOL_VERSION = "HTTP/1.1";

    private final String protocolVersion;

    private final StatusCode statusCode;

    public StatusLine() {
        this.protocolVersion = "";
        this.statusCode = null;
    }

    public StatusLine(StatusCode statusCode) {
        this(statusCode, DEFAULT_PROTOCOL_VERSION);
    }

    public StatusLine(StatusCode statusCode, String protocolVersion) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
    }

    public String asString() {
        return protocolVersion + " " + statusCode.getCode() + " " + statusCode
            .getStatusText();
    }
}
