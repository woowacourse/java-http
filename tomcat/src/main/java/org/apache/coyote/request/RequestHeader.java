package org.apache.coyote.request;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeader {
    private final Map<String, String> headers;

    public RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(final List<String> requestLines) {
        final Map<String, String> headers = new LinkedHashMap<>();
        for (String line : requestLines) {
            if(!line.contains(":")){
                break;
            }
            final String[] header = line.split(":", 2);
            headers.put(header[0], header[1]);
        }
        return new RequestHeader(headers);
    }

    public int getContentLength() {
        if (!headers.containsKey("Content-Length")) {
            return 0;
        }
        final String contentLength = headers.get("Content-Length").strip();
        return Integer.parseInt(contentLength);
    }

    @Override
    public String toString() {
        return headers.keySet()
                .stream()
                .map(key -> key + ":" + headers.get(key))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
