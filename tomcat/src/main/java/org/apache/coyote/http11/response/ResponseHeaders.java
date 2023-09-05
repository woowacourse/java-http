package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeaders {

    private final Map<String, String> headerMap = new LinkedHashMap<>();

    public void addHeader(String headerKey, String headerValue) {
        headerMap.put(headerKey, headerValue);
    }

    public String getHeadersForResponse() {
        return headerMap.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
