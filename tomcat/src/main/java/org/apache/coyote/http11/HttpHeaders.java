package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String HEADER_DELIMETER = ": ";

    private final Map<HttpHeaderName, String> headers = new LinkedHashMap<>();

    public HttpHeaders() {
    }

    public void putHeader(HttpHeaderName key, String value) {
        headers.put(key, value);
    }

    public void putHeader(String headerLine) {
        String[] headerParts = headerLine.split(HEADER_DELIMETER);
        HttpHeaderName headerName = HttpHeaderName.from(headerParts[0].trim());
        String headerValue = headerParts[1].trim();
        headers.put(headerName, headerValue);
    }

    public String getHeaderValue(HttpHeaderName headerName) {
        return headers.get(headerName);
    }

    public String resolveHeadersMessage() {
        StringBuilder headersMessage = new StringBuilder();
        for (HttpHeaderName key : headers.keySet()) {
            String headerLine = String.join(HEADER_DELIMETER, key.getValue(), headers.get(key));
            headersMessage.append(headerLine);
            headersMessage.append("\r\n");
        }

        return headersMessage.toString();
    }
}
