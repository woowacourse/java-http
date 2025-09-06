package org.apache.coyote.httpResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponseHeader {

    private final StatusLine statusLine;
    private final Map<String, String> headers;

    public HttpResponseHeader(
            final StatusLine statusLine,
            final String contentType
    ) {
        this.statusLine = statusLine;
        this.headers = new LinkedHashMap<>(Map.of("Content-Type", contentType));
    }

    public void addHeader(
            final String key,
            final String value
    ) {
        headers.put(key, value);
    }

    public String getHeaders() {
        final List<String> allHeaders = headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .toList();
        final List<String> result = new ArrayList<>(List.of(statusLine.getStatusLine()));
        result.addAll(allHeaders);

        return String.join(
                "\r\n",
                result
        );
    }
}
