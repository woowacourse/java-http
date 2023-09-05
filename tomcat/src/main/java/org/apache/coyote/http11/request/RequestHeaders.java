package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {

    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> headerMap;

    private RequestHeaders(final Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public static RequestHeaders from(final BufferedReader br) {
        Map<String, String> headerMap = br.lines()
                .takeWhile(inputHeaders -> !inputHeaders.equals(""))
                .map(inputHeaders -> inputHeaders.split(": "))
                .collect(Collectors.toMap(
                        inputHeaders -> inputHeaders[HEADER_KEY_INDEX],
                        inputHeaders -> inputHeaders[HEADER_VALUE_INDEX])
                );

        return new RequestHeaders(headerMap);
    }

    public boolean hasNotHeader(final String headerName) {
        return !headerMap.containsKey(headerName);
    }

    public String getHeaderValue(final String headerKey) {
        return headerMap.get(headerKey);
    }

    @Override
    public String toString() {
        return "RequestHeaders{" +
                "headerMap=" + headerMap +
                '}';
    }
}
