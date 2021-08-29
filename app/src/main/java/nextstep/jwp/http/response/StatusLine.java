package nextstep.jwp.http.response;

import nextstep.jwp.StatusCode;

public class StatusLine {

    private String protocol;
    private String statusCode;
    private String message;

    public StatusLine() {

    }

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
