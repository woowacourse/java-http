package org.apache.coyote.http11.component.response;

import org.apache.coyote.http11.component.common.body.Body;

public class HttpResponse {

    private static final String LINE_DELIMITER = "\r\n";

    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;
    private final Body body;

    public HttpResponse(final ResponseLine responseLine, final ResponseHeader responseHeader, final Body body) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public String getResponseText() {
        return responseLine.getResponseText() + LINE_DELIMITER + responseHeader.getResponseText() + LINE_DELIMITER
                + LINE_DELIMITER
                + body.deserialize();
    }

    public byte[] getResponseBytes() {
        return (responseLine.getResponseText() + LINE_DELIMITER + responseHeader.getResponseText() + LINE_DELIMITER
                + LINE_DELIMITER
                + body.deserialize()).getBytes();
    }
}
