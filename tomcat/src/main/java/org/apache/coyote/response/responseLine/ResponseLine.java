package org.apache.coyote.response.responseLine;

public class ResponseLine {

    //TODO: protocol version field 추가
    private HttpStatus httpStatus;

    public ResponseLine(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public ResponseLine() {}

    public String toCombine() {
        return "HTTP/1.1" + " " + httpStatus.toCombine();
    }

    public ResponseLine init(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }
}
