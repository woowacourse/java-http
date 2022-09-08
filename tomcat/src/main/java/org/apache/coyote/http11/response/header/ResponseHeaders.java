package org.apache.coyote.http11.response.header;

import static org.apache.coyote.http11.util.StringUtils.NEW_LINE;

import java.util.ArrayList;
import java.util.List;

public class ResponseHeaders {

    private List<HttpResponseHeader> values;

    public ResponseHeaders() {
        this.values = new ArrayList<>();
    }

    public ResponseHeaders(List<HttpResponseHeader> values) {
        this.values = values;
    }

    public static ResponseHeaders from(HttpResponseHeader first) {
        return new ResponseHeaders(List.of(first));
    }

    public static ResponseHeaders from(HttpResponseHeader first, HttpResponseHeader second) {
        return new ResponseHeaders(List.of(first, second));
    }

    public String toResponseFormat() {
        StringBuilder responseFormat = new StringBuilder();
        for (HttpResponseHeader header : values) {
            responseFormat.append(header.toHeaderFormat())
                    .append(NEW_LINE);
        }
        return responseFormat.toString();
    }

    public void add(HttpResponseHeader httpResponseHeader) {
        values.add(httpResponseHeader);
    }
}
