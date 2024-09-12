package org.apache.coyote.http11.response.line;

import org.apache.coyote.http11.HttpProtocol;

public class ResponseLine {

    private HttpStatus httpStatus;
    private HttpProtocol protocol;

    public ResponseLine(HttpStatus httpStatus, HttpProtocol protocol) {
        this.httpStatus = httpStatus;
        this.protocol = protocol;
    }

    public static ResponseLine createEmptyResponseLine() {
        return new ResponseLine(null, HttpProtocol.HTTP_11);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static ResponseLine createOkResponseLine() {
        return new ResponseLine(HttpStatus.OK, HttpProtocol.HTTP_11);
    }

    public static ResponseLine createUnauthorizedLine() {
        return new ResponseLine(HttpStatus.UNAUTHORIZED, HttpProtocol.HTTP_11);
    }

    public static ResponseLine createFoundLine() {
        return new ResponseLine(HttpStatus.FOUND, HttpProtocol.HTTP_11);
    }

    public String resolveLineMessage() {
        return String.join(
                " ",
                protocol.getHttpMessage(),
                String.valueOf(httpStatus.getCode()),
                httpStatus.getStatusMessage()
        );
    }
}
