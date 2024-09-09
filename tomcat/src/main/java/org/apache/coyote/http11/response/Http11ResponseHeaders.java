package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Http11ResponseHeaders {

    private final List<Http11ResponseHeader> headers;

    public Http11ResponseHeaders() {
        this.headers = new ArrayList<>();
    }

    public void add(String name, String value) {
        headers.add(new Http11ResponseHeader(name, value));
    }

    @Override
    public String toString() {
        return headers.stream()
                .map(header -> header.name() + ": " + String.join(";", header.value()) + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
