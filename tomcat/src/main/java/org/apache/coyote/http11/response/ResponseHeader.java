package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ResponseHeader {

    private static final String HEADER_DELIMITER = ": ";
    private static final String CRLF = "\r\n";
    private static final String END_OF_HEADER = " ";

    private final Map<String, String> headers;

    public ResponseHeader() {
        this.headers = new HashMap<>();
    }

    public void addHeader(String headerType, String headerValue) {
        headers.put(headerType, headerValue);
    }

    public String headerToResponse() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> header : headers.entrySet()) {
            stringBuilder.append(header.getKey())
                    .append(HEADER_DELIMITER)
                    .append(header.getValue())
                    .append(END_OF_HEADER)
                    .append(CRLF);
        }
        return stringBuilder.toString();
    }
}
