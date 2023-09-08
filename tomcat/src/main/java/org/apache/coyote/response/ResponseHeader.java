package org.apache.coyote.response;

import java.util.List;
import java.util.stream.Collectors;

public class ResponseHeader {

    private final List<String> headers;

    private ResponseHeader(final List<String> headers) {
        this.headers = headers;
    }

    public static ResponseHeader from(final List<Object> responseHeader) {
        final List<String> headers = responseHeader.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        return new ResponseHeader(headers);
    }

    @Override
    public String toString() {
        return headers.stream()
                .collect(Collectors.joining(" " + System.lineSeparator()));
    }
}
