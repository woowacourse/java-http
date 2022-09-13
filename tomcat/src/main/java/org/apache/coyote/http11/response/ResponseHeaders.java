package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.HttpHeader;

public class ResponseHeaders {

    private final List<HttpHeader> httpHeaders;

    public ResponseHeaders() {
        this.httpHeaders = new ArrayList<>();
    }

    private ResponseHeaders(List<HttpHeader> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public static ResponseHeaders from(HttpHeader... headers) {
        return new ResponseHeaders(List.of(headers));
    }

    public void add(HttpHeader... headers) {
        for (HttpHeader header : headers) {
            httpHeaders.add(header);
        }
    }

    @Override
    public String toString() {
        return httpHeaders.stream()
                .map(HttpHeader::generateHeaderResponse)
                .collect(Collectors.joining());
    }
}
