package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String HEADER_DELIMETER = ": ";
    private final Map<String, String> headers = new HashMap<>();

    public HttpHeaders() {
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
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
