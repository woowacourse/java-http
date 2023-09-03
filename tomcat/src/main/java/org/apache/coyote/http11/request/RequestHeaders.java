package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {

    private final Map<String, Object> headerMap;

    private RequestHeaders(final Map<String, Object> headerMap) {
        this.headerMap = headerMap;
    }

    public static RequestHeaders from(final BufferedReader br) {
        Map<String, Object> headerMap = br.lines()
                .takeWhile(line -> !line.equals(""))
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));

        return new RequestHeaders(headerMap);
    }

    public boolean hasNotHeader(final String headerName) {
        return !headerMap.containsKey(headerName);
    }

    public Object getHeaderValue(final String headerKey) {
        return headerMap.get(headerKey);
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
                "headerMap=" + headerMap +
                '}';
    }
}
