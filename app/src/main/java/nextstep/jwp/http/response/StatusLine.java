package nextstep.jwp.http.response;

public class StatusLine {

    private final String protocol;
    private final String statusCode;
    private final String message;

    public StatusLine(StatusCode statusCode) {
        this.protocol = "HTTP/1.1";
        this.statusCode = statusCode.getCode();
        this.message = statusCode.getMessage();
    }

    @Override
    public String toString() {
        return String.join(" ", protocol, statusCode, message) + " ";
    }
}
