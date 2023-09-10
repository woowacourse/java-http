package org.apache.coyote.http11.request;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestHeaders {

    private final List<RequestHeader> requestHeaders;

    private RequestHeaders(final List<RequestHeader> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public static RequestHeaders from(final Stream<String> request) {
        final List<RequestHeader> requestRequestHeaders = request.map(RequestHeader::from)
                                                                 .collect(Collectors.toList());

        return new RequestHeaders(requestRequestHeaders);
    }

    public List<RequestHeader> getHeaders() {
        return requestHeaders;
    }
}
