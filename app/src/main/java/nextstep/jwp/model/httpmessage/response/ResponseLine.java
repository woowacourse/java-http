package nextstep.jwp.model.httpmessage.response;


public class ResponseLine {

    private final HttpStatus httpStatus;
    private final String protocol;

    public ResponseLine(HttpStatus status, String protocol) {
        this.httpStatus = status;
        this.protocol = protocol;
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }

    public String getProtocol() {
        return protocol;
    }
}
