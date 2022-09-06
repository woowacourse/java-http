package org.apache.coyote.support;

import java.util.LinkedList;

public class HttpResponse {

    private final LinkedList<String> responseHeaders = new LinkedList<>();
    private boolean hasBody = false;

    private HttpResponse() {
    }

    public static HttpResponse builder() {
        return new HttpResponse();
    }

    public HttpResponse add(HttpHeader header, Object value) {
        final String responseHeader = header.apply(value.toString());
        responseHeaders.add(responseHeader);
        return this;
    }

    public HttpResponse body(String body) {
        add(HttpHeader.CONTENT_LENGTH, body.getBytes().length);
        responseHeaders.add("");
        responseHeaders.add(body);
        hasBody = true;
        return this;
    }

    public String build() {
        if (!hasBody) {
            responseHeaders.add("");
        }
        return String.join("\r\n", responseHeaders);
    }
}
