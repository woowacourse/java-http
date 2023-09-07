package org.apache.common;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeader {

    private final Map<String, String> headers;

    public HttpHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeader from(List<String> headers) {
        return new HttpHeader(
                headers.stream()
                        .map(header -> header.split(": "))
                        .collect(Collectors.toMap(value -> value[0], value -> value[1]))
        );
    }

    public boolean hasHeader(String headerName) {
        return this.headers.containsKey(headerName);
    }

    public String findValue(String headerName) {
        return this.headers.get(headerName);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
                "headers=" + headers +
                '}';
    }
}
