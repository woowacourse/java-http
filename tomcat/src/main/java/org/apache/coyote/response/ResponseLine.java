package org.apache.coyote.response;

import org.apache.coyote.http11.Protocol;

public class ResponseLine {

    private static final String LINE_SPLIT_DELIMITER = " ";

    private Protocol protocol;
    private HttpStatus httpStatus;

    public ResponseLine() {
        this.protocol = Protocol.HTTP1_1;
        this.httpStatus = null;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getResponseLine() {
        return protocol.getValue() + LINE_SPLIT_DELIMITER + httpStatus.getStatusCode() + LINE_SPLIT_DELIMITER
                + httpStatus.getStatus();
    }
}
