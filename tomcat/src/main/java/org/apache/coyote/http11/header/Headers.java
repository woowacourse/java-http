package org.apache.coyote.http11.header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private static final String CRLF = "\r\n";

    private final Map<String, HttpHeader> headers;

    public Headers() {
        this.headers = new HashMap<>();
    }

    public void add(String rawHeader) {
        add(new HttpHeader(rawHeader));
    }

    public void add(HttpHeader header) {
        headers.put(header.getName(), header);
    }

    public HttpHeader get(String name) {
        return headers.getOrDefault(name, HttpHeader.EMPTY);
    }

    public int getContentLength() {
        HttpHeader contentLengthHeader = get("Content-Length");
        if (contentLengthHeader.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(contentLengthHeader.getValue());
    }

    public List<HttpHeader> getHeaders() {
        return new ArrayList<>(headers.values());
    }

    public String toLine() {
        return headers.values().stream()
                .map(HttpHeader::toLine)
                .collect(Collectors.joining(CRLF));
    }
}
