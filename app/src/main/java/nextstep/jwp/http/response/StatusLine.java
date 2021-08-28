package nextstep.jwp.http.response;

public class StatusLine {
    private String protocol;
    private String statusCode;
    private String statusMessage;

    public StatusLine() {
    }

    public StatusLine(Status status) {
        this.protocol = "HTTP/1.1";
        this.statusCode = status.getCode();
        this.statusMessage = status.name();
    }

    @Override
    public String toString() {
        return String.join(" ", protocol, statusCode, statusMessage, "");
    }
}
