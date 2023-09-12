package org.apache.coyote.response.header;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseHeader {

    private final List<String> headers;

    private ResponseHeader(final List<String> headers) {
        this.headers = headers;
    }

    public static ResponseHeader create() {
        return new ResponseHeader(new ArrayList<>());
    }

    public static ResponseHeader from(final List<Object> responseHeader) {
        final List<String> headers = responseHeader.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        return new ResponseHeader(headers);
    }

    public void add(Object object) {
        headers.add(String.valueOf(object));
    }

    @Override
    public String toString() {
        return String.join(" \r\n", headers);
    }
}
