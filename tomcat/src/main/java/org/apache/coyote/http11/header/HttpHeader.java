package org.apache.coyote.http11.header;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpHeader {

    public static final String HEADER_DELIMITER = ":";

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
                .collect(Collectors.toMap(
                        r -> r.split(HEADER_DELIMITER)[0],
                        r -> r.split(HEADER_DELIMITER)[1].trim())
                );

        return new HttpHeader(headers);
    }

    public void addHeader(HttpHeaderName headerName, String value) {
        headers.put(headerName.getHeaderName(), value);
    }

    public String getHeader(HttpHeaderName headerName) {
        return headers.get(headerName.getHeaderName());
    }

    public String toHttpHeader() {
        List<String> httpHeader = new ArrayList<>();

        for (Entry<String, String> entry : headers.entrySet()) {
            httpHeader.add(entry.getKey() + HEADER_DELIMITER + " " + entry.getValue() + " ");
        }

        return String.join("\r\n", httpHeader);
    }
}
