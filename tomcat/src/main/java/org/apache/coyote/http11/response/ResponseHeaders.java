package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private final List<ResponseHeader> responseHeaders;

    public ResponseHeaders() {
        this.responseHeaders = new ArrayList<>();
    }

    public void add(final String name, final String value) {
        final ResponseHeader responseHeader = new ResponseHeader(name, value);
        responseHeaders.add(responseHeader);
    }

    public String toMessage() {
        return responseHeaders.stream()
                              .map(ResponseHeader::toMessage)
                              .collect(Collectors.joining(System.lineSeparator()));
    }
}
