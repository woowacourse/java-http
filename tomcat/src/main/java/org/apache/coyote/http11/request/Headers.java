package org.apache.coyote.http11.request;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Headers {

    private final List<Header> headers;

    private Headers(final List<Header> headers) {
        this.headers = headers;
    }

    public static Headers from(final Stream<String> request) {
        final List<Header> requestHeaders = request.map(Header::from)
                                                   .collect(Collectors.toList());

        return new Headers(requestHeaders);
    }

    public List<Header> getHeaders() {
        return headers;
    }
}
