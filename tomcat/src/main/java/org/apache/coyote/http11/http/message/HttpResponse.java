package org.apache.coyote.http11.http.message;

import org.apache.coyote.http11.http.util.HttpResponseMessageHeader;
import org.apache.coyote.http11.http.util.ReasonPhrase;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String SP = " ";
    private static final String CRLF = "\r\n";

    private ReasonPhrase reasonPhrase;
    private final ResponseHeaders responseHeaders = new ResponseHeaders();
    private String messageBody;

    public void setReasonPhrase(final ReasonPhrase reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public void setMessageHeaders(final HttpResponseMessageHeader headerType, final String value) {
        responseHeaders.setAttribute(headerType, value);
    }

    public void setMessageBody(final String messageBody) {
        this.messageBody = messageBody;
    }

    @Override
    public String toString() {
        return joinResponses(
                createStatusLine(),
                responseHeaders.toString(),
                messageBody.toString()
        );
    }

    public String joinResponses(final String... responses) {
        return String.join(
                CRLF,
                responses
        );
    }

    private String createStatusLine() {
        return String.join(
                SP,
                HTTP_VERSION,
                Integer.toString(this.reasonPhrase.getStatusCode()),
                this.reasonPhrase.getValue() + SP
        );
    }
}
