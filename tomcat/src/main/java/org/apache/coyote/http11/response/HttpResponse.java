package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.MessageBody;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final MessageBody messageBody;

    public HttpResponse(final StatusLine statusLine, final ResponseHeaders responseHeaders, final MessageBody messageBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.messageBody = messageBody;
    }

    public byte[] convertToBytes() {
        return String.join("\r\n",
                statusLine.getStatusLineForResponse(),
                responseHeaders.getHeadersForResponse(),
                "",
                messageBody.getValue()).getBytes();
    }
}
