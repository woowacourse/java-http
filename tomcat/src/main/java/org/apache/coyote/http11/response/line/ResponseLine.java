package org.apache.coyote.http11.response.line;

import org.apache.coyote.http11.HttpProtocol;

public class ResponseLine {

    private final HttpStatus httpStatus;
    private final HttpProtocol protocol;

    public ResponseLine(final HttpStatus httpStatus, final HttpProtocol protocol) {
        this.httpStatus = httpStatus;
        this.protocol = protocol;
    }

    public static ResponseLine createOkResponseLine() {
        return new ResponseLine(HttpStatus.OK, HttpProtocol.HTTP_11);
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
