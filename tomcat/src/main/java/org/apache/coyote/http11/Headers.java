package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    private final Map<String, String> headers = new HashMap<>();

    public void addHeader(final String headerLine) {
        parseHeader(headerLine);
    }

    private void parseHeader(final String headerLine) {
        // header 값에 :가 포함 된 경우 고려
        // ex ipv6: 2001:0db8:85a3:0000:0000:8a2e:0370:7334
        final String[] parts = headerLine.split(":", 2);
        if (parts.length == 2) {
            final String headerName = parts[0].trim().toLowerCase();
            final String headerValue = parts[1].trim();
            headers.put(headerName, headerValue);
        }
    }

    public String getHeader(final String headerName) {
        return headers.get(headerName.toLowerCase());
    }
}
