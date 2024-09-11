package org.apache.http.header;

import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpHeaders that = (HttpHeaders) o;
        return Objects.deepEquals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(headers);
    }
}
