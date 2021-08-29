package nextstep.jwp.http.response;

import nextstep.jwp.http.response.type.StatusCode;

public class StatusLine {
    private String protocol;
    private String statusCode;
    private String statusMessage;

    public StatusLine() {
    }

    public StatusLine(StatusCode statusCode) {
        this.protocol = "HTTP/1.1";
        this.statusCode = statusCode.getCode();
        this.statusMessage = statusCode.getMessage();
    }

    @Override
    public String toString() {
        return String.join(" ", protocol, statusCode, statusMessage) + " ";
    }
}
