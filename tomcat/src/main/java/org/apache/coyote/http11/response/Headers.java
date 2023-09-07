package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Headers {

    private List<Header> headers = new ArrayList<>();

    public Headers() {
    }

    public void add(final String name, final String value) {
        final Header header = new Header(name, value);
        headers.add(header);
    }

    public String toMessage() {
        return headers.stream()
                      .map(Header::toMessage)
                      .collect(Collectors.joining("\r\n"));
    }

    public List<Header> getHeaders() {
        return headers;
    }
}
