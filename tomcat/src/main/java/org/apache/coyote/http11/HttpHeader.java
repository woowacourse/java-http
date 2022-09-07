package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class HttpHeader {

    private static final String FORM_DATA_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final Map<String, String> headers;

    public HttpHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeader of(final Queue<String> rawHeader) {
        final Map<String, String> parseHeaders = new HashMap<>();
        for (final String header : rawHeader) {
            final String[] parsedHeader = header.split(": ");
            parseHeaders.put(parsedHeader[0], parsedHeader[1].trim());
        }

        return new HttpHeader(parseHeaders);
    }

    public boolean isFormDataType() {
        return headers.containsKey("Content-Type") && headers.get("Content-Type").equals(FORM_DATA_CONTENT_TYPE);
    }

    public void addHeader(final String headerName, final String value) {
        headers.put(headerName, value);
    }

    public String getHeader(final String headerName) {
        return headers.get(headerName);
    }

    public boolean contains(final String headerName) {
        return headers.containsKey(headerName);
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
