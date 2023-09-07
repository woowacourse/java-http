package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;

public class ResponseLine {

    private final HttpStatus httpStatus;

    public ResponseLine(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String convertStatusLine() {
        String statusLineFormat = "HTTP/1.1 %s ";

        return String.format(statusLineFormat, httpStatus.getHttpStatus());
    }

}
