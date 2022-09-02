package org.apache.coyote.http11.message.response;

import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.common.HttpVersion;

public class ResponseMessage {

    private static final String RESPONSE_LINE_FORMAT = "%s %s %s";

    private static final String NEW_LINE = "\r\n";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final HttpHeaders httpHeaders;
    private final String body;

    public ResponseMessage(final HttpVersion httpVersion, final HttpStatus httpStatus, final HttpHeaders httpHeaders,
                           final String body) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public String generateMessage() {
        return generateResponseLine() + NEW_LINE
                + httpHeaders.generateHeaderText() + NEW_LINE
                + body;
    }

    private String generateResponseLine() {
        return String.format(RESPONSE_LINE_FORMAT, httpVersion.getName(), httpStatus.getCode(), httpStatus.name());
    }
}
