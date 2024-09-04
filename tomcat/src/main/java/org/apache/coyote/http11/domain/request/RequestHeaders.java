package org.apache.coyote.http11.domain.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(List<String> headerLines) {
        this.headers = new HashMap<>(parseHeaders(headerLines));
    }

    private Map<String, String> parseHeaders(List<String> headerLines) {
        return headerLines.stream()
                .filter(header -> header.contains(":"))
                .collect(Collectors.toMap(
                        header -> header.substring(0, header.indexOf(":")).trim(),
                        header -> header.substring(header.indexOf(":") + 1).trim())
                );
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
