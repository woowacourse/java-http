package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeader {
    private final Map<String, String> headers;

    public RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(final List<String> lines) {
        Map<String, String> headers = new HashMap<>();
        for (String line : lines) {
            String[] headerInfo = line.split(": ");
            headers.put(headerInfo[0], headerInfo[1]);
        }
        return new RequestHeader(headers);
    }

    public String getHeaderValue(final String header) {
        if (!headers.containsKey(header)) {
            return null;
        }
        return headers.get(header);
    }
}

