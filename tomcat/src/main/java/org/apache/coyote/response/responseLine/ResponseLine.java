package org.apache.coyote.response.responseLine;

public class ResponseLine {

    //TODO: protocol version field 추가
    private final HttpStatus httpStatus;

    public ResponseLine(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String toCombine(){
        return "HTTP/1.1" + " " + httpStatus.toCombine();
    }

}
