package org.apache.coyote.http11.request;

import java.util.LinkedHashMap;
import java.util.List;

public record RequestHeaders(LinkedHashMap<String, String> headers) {

    public static RequestHeaders parse(final List<String> headerLines) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();

        for (String line : headerLines) {
            final String[] header = line.split(":", 2);
            if (header.length == 2) {
                headers.put(header[0].trim(), header[1].trim());
            }
        }

        return new RequestHeaders(headers);
    }

    public int getContentLength() {
        return headers.containsKey("Content-Length") ? Integer.parseInt(headers.get("Content-Length")) : 0;
    }
}
