package org.apache.coyote.http11.http.message;

import org.apache.coyote.http11.http.util.HttpResponseMessageHeader;
import org.apache.coyote.http11.http.util.ReasonPhrase;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String MESSAGE_HEADERS_DELIMITER = ": ";
    private static final String SP = " ";
    private static final String CRLF = "\r\n";

    private ReasonPhrase reasonPhrase;
    private Map<HttpResponseMessageHeader, String> messageHeaders;
    private String messageBody;

    public HttpResponse() {
        this.messageHeaders = new HashMap<>();
    }

    public void setReasonPhrase(final ReasonPhrase reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public void setMessageHeaders(final HttpResponseMessageHeader headerType, final String value) {
        this.messageHeaders.put(headerType, value);
    }

    public void setMessageBody(final String messageBody) {
        this.messageBody = messageBody;
    }

    @Override
    public String toString() {
        return joinResponses(
                createStatusLine(),
                createMessageHeaders(),
                messageBody
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

    private String createMessageHeaders() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final HttpResponseMessageHeader header : messageHeaders.keySet()) {
            stringBuilder.append(header.getValue())
                         .append(MESSAGE_HEADERS_DELIMITER)
                         .append(messageHeaders.get(header))
                         .append(SP)
                         .append(CRLF);
        }
        return stringBuilder.toString();
    }
}
