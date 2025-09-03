package org.apache.coyote.response;

public class ResponseInfo {

    //TODO: protocol version field 추가
    private final HttpStatus httpStatus;

    public ResponseInfo(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String toCombine(){
        return "HTTP/1.1" + " " + httpStatus.toCombine();
    }

}
