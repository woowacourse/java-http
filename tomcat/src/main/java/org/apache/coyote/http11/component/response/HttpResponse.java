package org.apache.coyote.http11.component.response;

import org.apache.coyote.http11.component.common.body.Body;

public class HttpResponse {

    private static final String LINE_DELIMITER = "\r\n";

    private final StatusLine statusLine;
    private final ResponseHeader responseHeader;
    private final Body body;

    public HttpResponse(final StatusLine statusLine, final ResponseHeader responseHeader, final Body body) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public String getResponseText() {
        return statusLine.getResponseText() + LINE_DELIMITER + responseHeader.getResponseText() + LINE_DELIMITER
                + LINE_DELIMITER
                + body.deserialize();
    }

    public byte[] getResponseBytes() {
        return (statusLine.getResponseText() + LINE_DELIMITER + responseHeader.getResponseText() + LINE_DELIMITER
                + LINE_DELIMITER
                + body.deserialize()).getBytes();
    }
}
