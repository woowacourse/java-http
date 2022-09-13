package org.apache.coyote.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.support.HttpHeader;

public class ResponseHeaders {

    private final Map<HttpHeader, String> headers;

    public ResponseHeaders(Map<HttpHeader, String> headers) {
        this.headers = headers;
    }

    public void addHeader(HttpHeader name, String value) {
        headers.put(name, value);
    }

    public List<String> toMessageLines() {
        return headers.keySet().stream()
                .map(it -> it.toLineFormat(headers.get(it)))
                .collect(Collectors.toList());
    }
}
