package org.apache.coyote.http11.model;

import java.util.List;
import java.util.stream.Collectors;

public class Headers {

    private final List<Header> headers;

    public Headers(final List<Header> headers) {
        this.headers = headers;
    }

    public void add(final Header header) {
        this.headers.add(header);
    }

    public String getString() {
        List<String> headerStrings = headers.stream()
                .map(Header::getString)
                .collect(Collectors.toList());
        return String.join("\r\n", headerStrings);
    }
}
