package nextstep.jwp.http.response;

public class ResponseLine {

    private final String protocol;
    private final int code;
    private final String message;

    public ResponseLine(HttpStatus httpStatus) {
        this("HTTP/1.1", httpStatus.getCode(), httpStatus.getMessage());
    }

    private ResponseLine(String protocol, int code, String message) {
        this.protocol = protocol;
        this.code = code;
        this.message = message;
    }

    public String getProtocol() {
        return protocol;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
