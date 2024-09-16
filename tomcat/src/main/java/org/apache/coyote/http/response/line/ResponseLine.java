package org.apache.coyote.http.response.line;

import org.apache.coyote.http.HttpProtocol;

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

    public String resolveLineMessage() {
        return String.join(
                " ",
                protocol.getHttpMessage(),
                String.valueOf(httpStatus.getCode()),
                httpStatus.getStatusMessage()
        );
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
