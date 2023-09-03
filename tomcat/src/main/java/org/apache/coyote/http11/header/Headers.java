package org.apache.coyote.http11.header;

import java.util.*;

public class Headers {

    private final Map<Header, String> headers = new LinkedHashMap<>();

    public void addHeader(final Header header,
                            final String value) {
        headers.put(header, value);
    }

    public String parseResponse() {
        final List<String> stringHeaders = new ArrayList<>();
        for (Header header : headers.keySet()) {
            final String format = String.format("%s: %s ", header.getValue(), headers.get(header));
            stringHeaders.add(format);
        }
        return String.join("\r\n", stringHeaders);
    }
}
