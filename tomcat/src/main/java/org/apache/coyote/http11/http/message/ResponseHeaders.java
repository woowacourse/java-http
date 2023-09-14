package org.apache.coyote.http11.http.message;

import org.apache.coyote.http11.http.util.HttpResponseMessageHeader;

import java.util.EnumMap;
import java.util.Map;

public class ResponseHeaders {

    private static final String MESSAGE_HEADERS_DELIMITER = ": ";
    private static final String SP = " ";
    private static final String CRLF = "\r\n";

    private final Map<HttpResponseMessageHeader, String> headerValues;

    public ResponseHeaders() {
        this.headerValues = new EnumMap<>(HttpResponseMessageHeader.class);
    }

    public void setAttribute(final HttpResponseMessageHeader headerType, final String value) {
        headerValues.put(headerType, value);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final HttpResponseMessageHeader header : headerValues.keySet()) {
            stringBuilder.append(header.getValue())
                         .append(MESSAGE_HEADERS_DELIMITER)
                         .append(headerValues.get(header))
                         .append(SP)
                         .append(CRLF);
        }
        return stringBuilder.toString();
    }
}
