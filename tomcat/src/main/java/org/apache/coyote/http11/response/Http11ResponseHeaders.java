package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpHeaderName;

public class Http11ResponseHeaders {

    private final List<Http11ResponseHeader> headers;

    public Http11ResponseHeaders() {
        this.headers = new ArrayList<>();
    }

    public void add(HttpHeaderName name, String value) {
        headers.add(new Http11ResponseHeader(name, value));
    }

    @Override
    public String toString() {
        return headers.stream()
                .map(header -> header.name().getValue() + ": " + String.join(";", header.value()) + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
