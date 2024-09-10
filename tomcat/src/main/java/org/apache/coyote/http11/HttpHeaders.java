package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String HEADER_DELIMETER = ": ";

    private final Map<String, String> headers = new LinkedHashMap<>();

    public HttpHeaders() {
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

    public void putHeader(String headerLine) {
        String[] headerParts = headerLine.split(HEADER_DELIMETER);
        headers.put(headerParts[0], headerParts[1]);
    }

    public String getHeaderValue(String key) {
        return headers.get(key);
    }

    public String resolveHeadersMessage() {
        StringBuilder headersMessage = new StringBuilder();
        for (String key : headers.keySet()) {
            String headerLine = String.join(HEADER_DELIMETER, key, headers.get(key));
            headersMessage.append(headerLine);
            headersMessage.append("\r\n");
        }

        return headersMessage.toString();
    }
}
