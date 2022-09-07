package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class HttpResponseHeader {

    private final Map<String, String> headers;

    public HttpResponseHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(final String headerName, final String value) {
        headers.put(headerName, value);
    }

    public String getHeader(final String headerName) {
        return headers.get(headerName);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        for (final String name : headers.keySet()) {
            stringBuilder.append(name).append(": ").append(headers.get(name));
            appendCharsetToContentType(stringBuilder, name);
            stringBuilder.append(" \r\n");
        }

        return stringBuilder.toString();
    }

    private void appendCharsetToContentType(final StringBuilder stringBuilder, final String name) {
        if (name.equals("Content-Type")) {
            stringBuilder.append(";charset=utf-8");
        }
    }
}
