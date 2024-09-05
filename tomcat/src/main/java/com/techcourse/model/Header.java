package com.techcourse.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Header {

    private final Map<String, String> headers;

    public static Header of(String bulkHeaders) {
        List<String> splitHeaders = List.of(bulkHeaders.split("\r\n"));
        return of(splitHeaders);
    }

    public static Header of(List<String> splitHeaders) {
        Map<String, String> headers = splitHeaders.stream()
                .filter(header -> header.contains(":"))
                .map(header -> header.split(": "))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
        return new Header(headers);
    }

    private Header(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getKey(String header) {
        if (!headers.containsKey(header)) {
            throw new IllegalArgumentException("Header " + header + " not found");
        }
        return headers.get(header);
    }
}
