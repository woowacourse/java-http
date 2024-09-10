package org.apache.coyote.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpResponseHeaders {

    private final List<HttpResponseHeader> headers;

    public HttpResponseHeaders() {
        this.headers = new ArrayList<>();
    }

    public void add(String name, String value) {
        headers.add(new HttpResponseHeader(name, value));
    }

    @Override
    public String toString() {
        return headers.stream()
                .map(header -> header.name() + ": " + String.join(";", header.value()) + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
