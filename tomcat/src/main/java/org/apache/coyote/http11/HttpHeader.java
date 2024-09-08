package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpHeader {

    private final Map<String, String> headers;

    public HttpHeader() {
        this.headers = new LinkedHashMap<>();
    }

    public HttpHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeader from(List<String> request) {
        Map<String, String> headers = request.stream()
                .skip(1)
                .collect(Collectors.toMap(r -> r.split(":")[0], r -> r.split(":")[1].trim()));
        return new HttpHeader(headers);
    }

    public void addHeader(String headerName, String value) {
        headers.put(headerName, value);
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public String toHttpHeader() {
        List<String> httpHeader = new ArrayList<>();

        for (Entry<String, String> entry : headers.entrySet()) {
            httpHeader.add(entry.getKey() + ": " + entry.getValue() + " ");
        }

        return String.join("\r\n", httpHeader);
    }
}
