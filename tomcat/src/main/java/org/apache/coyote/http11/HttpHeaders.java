package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private static final int HEADER_END_NOT_FOUND = -1;

    private final Map<String, HttpHeader> headers;

    private HttpHeaders(final Map<String, HttpHeader> headers) {
        this.headers = headers;
    }

    public static HttpHeaders parse(final List<String> headerLines) {
        Map<String, HttpHeader> headers = new HashMap<>();
        int headerEndIndex = findHeaderEnd(headerLines);
        for (String headerLine : headerLines.subList(0, headerEndIndex)) {
            HttpHeader header = HttpHeader.parse(headerLine);
            headers.put(header.getName(), header);
        }
        return new HttpHeaders(headers);
    }

    private static int findHeaderEnd(final List<String> headerLines) {
        int index = headerLines.indexOf("");
        if (index == HEADER_END_NOT_FOUND) {
            return headerLines.size();
        }
        return index;
    }

    public String getOrDefault(final String name, final String defaultValue) {
        HttpHeader header = headers.get(name);
        if (header == null) {
            return defaultValue;
        }
        return header.getValue();
    }

    public List<HttpHeader> getAll() {
        return new ArrayList<>(headers.values());
    }
}
