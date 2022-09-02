package org.apache.coyote.http11.message.request;

import lombok.Getter;
import lombok.ToString;
import org.apache.coyote.http11.message.common.HttpHeaders;

@ToString
@Getter
public class RequestMessage {

    private static final String NEW_LINE = "\r\n";
    private static final String HEADER_BODY_DELIMITER = NEW_LINE + NEW_LINE;

    private static final int REQUEST_LINE_AND_HEADER_INDEX = 0;
    private static final int REQUEST_START_LINE_INDEX = 0;
    private static final int HEADERS_INDEX = 1;
    private static final int BODY_INDEX = 1;

    private final RequestStartLine requestStartLine;
    private final HttpHeaders httpHeaders;
    private final String requestBody;

    public RequestMessage(final String message) {
        String[] splitHeaderBody = message.split(HEADER_BODY_DELIMITER, -1);
        String requestLineAndHeader = splitHeaderBody[REQUEST_LINE_AND_HEADER_INDEX];
        String[] splitByNewLine = requestLineAndHeader.split(NEW_LINE, 2);

        this.requestStartLine = new RequestStartLine(splitByNewLine[REQUEST_START_LINE_INDEX]);
        this.httpHeaders = new HttpHeaders(splitByNewLine[HEADERS_INDEX]);
        this.requestBody = splitHeaderBody[BODY_INDEX];
    }

    public RequestUri getRequestUri() {
        return requestStartLine.getRequestUri();
    }
}
