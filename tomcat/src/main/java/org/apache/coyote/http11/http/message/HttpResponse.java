package org.apache.coyote.http11.http.message;

import org.apache.coyote.controller.util.Extension;
import org.apache.coyote.controller.util.FileResolver;
import org.apache.coyote.http11.http.util.HttpResponseMessageHeader;
import org.apache.coyote.http11.http.util.ReasonPhrase;

import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.http.util.HttpResponseMessageHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.http.util.HttpResponseMessageHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.http.util.HttpResponseMessageHeader.LOCATION;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String EMPTY_MESSAGE_BODY = "";
    private static final String MESSAGE_HEADERS_DELIMITER = ": ";
    private static final String SP = " ";
    private static final String CRLF = "\r\n";
    private static final String FILE_PATH_PREFIX = "/";

    private final ReasonPhrase reasonPhrase;
    private final Map<HttpResponseMessageHeader, String> messageHeaders;
    private final String messageBody;

    private HttpResponse(final ReasonPhrase reasonPhrase, final Map<HttpResponseMessageHeader, String> messageHeaders, final String messageBody) {
        this.reasonPhrase = reasonPhrase;
        this.messageHeaders = messageHeaders;
        this.messageBody = messageBody;
    }

    public static HttpResponse ofRedirect(final FileResolver file) {
        final HashMap<HttpResponseMessageHeader, String> messageHeaders = new HashMap<>();
        messageHeaders.put(LOCATION, FILE_PATH_PREFIX + file.getFileName());
        return new HttpResponse(ReasonPhrase.FOUND, messageHeaders, EMPTY_MESSAGE_BODY);
    }

    public static HttpResponse ofOk(final Extension messageBodyExtension, final String messageBody) {
        final HashMap<HttpResponseMessageHeader, String> messageHeaders = new HashMap<>();
        messageHeaders.put(CONTENT_LENGTH, Integer.toString(messageBody.getBytes().length));
        messageHeaders.put(CONTENT_TYPE, messageBodyExtension.getContentType());
        return new HttpResponse(ReasonPhrase.OK, messageHeaders, messageBody);
    }

    public HttpResponse addMessageHeader(final HttpResponseMessageHeader key, final String value) {
        messageHeaders.put(key, value);
        return this;
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
