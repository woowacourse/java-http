package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeader {

    private final Map<String, String> headers = new HashMap<>();

    public RequestHeader(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public static RequestHeader from(String header) {
        Map<String, String> headers = new HashMap<>();

        List<String[]> headerLines = Arrays.stream(header.split("\r\n"))
                .map(line -> (line.split(":")))
                .collect(Collectors.toList());

        for (String[] keyValue : headerLines) {
            headers.put(keyValue[0], keyValue[1]);
        }

        return new RequestHeader(headers);
    }

    public String getByKey(String key) {
        return headers.getOrDefault(key, null);
    }
}
