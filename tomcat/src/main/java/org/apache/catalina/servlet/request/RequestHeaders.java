package org.apache.catalina.servlet.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    private RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(List<String> headers) {
        Map<String, String> headerMap = new HashMap<>();
        for (String header : headers) {
            String[] nameAndValue = header.split(": ");
            validateHeaderValue(nameAndValue);
            headerMap.put(nameAndValue[0], nameAndValue[1]);
        }
        return new RequestHeaders(headerMap);
    }

    private static void validateHeaderValue(String[] nameAndValue) {
        if (nameAndValue.length != 2) {
            throw new InvalidHeaderException();
        }
    }

    public Map<String, String> headers() {
        return headers;
    }

    public boolean contains(String name) {
        return headers.containsKey(name);
    }

    public String get(String name) {
        return headers.getOrDefault(name, null);
    }
}
