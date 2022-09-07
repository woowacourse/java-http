package org.apache.coyote.http11.response;

public class General {

    private final HttpStatus httpStatus;

    public General(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String asString() {
        return "HTTP/1.1 " + this.httpStatus.getStatusCode() + " " + this.httpStatus.getName() + " ";
    }
}
