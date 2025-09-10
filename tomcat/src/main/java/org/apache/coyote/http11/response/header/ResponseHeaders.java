package org.apache.coyote.http11.response.header;

import java.util.ArrayList;
import java.util.List;

public class ResponseHeaders {

    private final List<ResponseHeader> headers;

    public ResponseHeaders(final List<ResponseHeader> headers) {
        this.headers = new ArrayList<>(headers);
    }

    public void add(final ResponseHeader header) {
        headers.add(header);
    }

    public String toResponseText() {
        final StringBuilder sb = new StringBuilder();
        headers.forEach(header -> {
            sb.append(header.toResponseTest())
                    .append(" \r\n");
        });

        return sb.toString();
    }
}
