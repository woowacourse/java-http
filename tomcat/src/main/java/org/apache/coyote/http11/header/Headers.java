package org.apache.coyote.http11.header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Headers {

    private final Map<String, HttpHeader> headers;

    public Headers() {
        this.headers = new HashMap<>();
    }

    public void add(String rawHeader) {
        add(new HttpHeader(rawHeader));
    }

    private void add(HttpHeader header) {
        headers.put(header.getName(), header);
    }

    public HttpHeader get(String name) {
        return headers.getOrDefault(name, HttpHeader.EMPTY);
    }

    public int getContentLength() {
        HttpHeader contentLength = get("Content-Length");
        if (contentLength.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(contentLength.getValue());
    }

    public List<HttpHeader> getHeaders() {
        return new ArrayList<>(headers.values());
    }
}
