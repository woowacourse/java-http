package nextstep.jwp.handler.response;

import nextstep.jwp.handler.constant.HttpStatus;

public class ResponseLine {
    private final String version;
    private HttpStatus status;

    public ResponseLine(String version) {
        this.version = version;
    }

    public void setHttpStatus(HttpStatus status) {
        this.status = status;
    }

    public String makeHttpMessage() {
        return version + " " + status.getValue() + " " + status.getReasonPhrase() + " ";
    }
}
