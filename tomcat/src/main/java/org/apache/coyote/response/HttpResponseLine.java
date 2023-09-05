package org.apache.coyote.response;

import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.common.HttpVersion;

public class HttpResponseLine {

    private static final String DELIMITER = " ";
    private static final String END_OF_LINE = "";

    private HttpVersion httpVersion;
    private HttpStatus httpStatus;

    public HttpResponseLine(final HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        this.httpStatus = HttpStatus.OK;
    }

    public void setHttpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public String toString() {
        return String.join(
                DELIMITER,
                httpVersion.getVersion(),
                String.valueOf(httpStatus.getCode()),
                httpStatus.getMessage(),
                END_OF_LINE
        );
    }
}
