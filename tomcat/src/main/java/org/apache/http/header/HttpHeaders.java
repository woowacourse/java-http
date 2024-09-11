package org.apache.http.header;

import java.util.Arrays;

public class HttpHeaders {
    private final HttpHeader[] headers;

    public HttpHeaders(HttpHeader[] headers) {
        this.headers = headers;
    }

    public static HttpHeaders from(String[] headers) {
        return new HttpHeaders(Arrays.stream(headers)
                .map(HttpHeader::from)
                .toArray(HttpHeader[]::new));
    }

    public HttpHeader[] getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(headers)
                .forEach(header -> stringBuilder.append(header.toString()).append("\r\n"));
        return stringBuilder.toString();
    }
}
