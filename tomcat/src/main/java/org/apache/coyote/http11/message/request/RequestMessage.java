package org.apache.coyote.http11.message.request;

import lombok.Getter;
import lombok.ToString;
import org.apache.coyote.http11.message.common.HttpHeaders;

@ToString
@Getter
public class RequestMessage {

    private static final String NEW_LINE = "\r\n";
    private static final String BODY_DELIMITER = NEW_LINE + NEW_LINE;

    private final RequestStartLine requestStartLine;
    private final HttpHeaders httpHeaders;
    private final String requestBody;

    public RequestMessage(final String message) {
        String[] splitHeaderBody = message.split(BODY_DELIMITER, -1);
        String requestLineAndHeader = splitHeaderBody[0];
        String[] splitByNewLine = requestLineAndHeader.split(NEW_LINE, 2);

        this.requestStartLine = new RequestStartLine(splitByNewLine[0]);
        this.httpHeaders = new HttpHeaders(splitByNewLine[1]);
        this.requestBody = splitHeaderBody[1];
    }

    public RequestUri getRequestUri() {
        return requestStartLine.getRequestUri();
    }
}
