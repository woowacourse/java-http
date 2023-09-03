package org.apache.coyote.requests;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class RequestHeader {
    private final Map<String, String> items;

    private RequestHeader(Map<String, String> items) {
        this.items = items;
    }

    public static RequestHeader from(String string) {
        return Arrays.stream(string.split("\r\n"))
                .map(line -> line.split(": "))
                .collect(collectingAndThen(
                        toMap(line -> line[0], line -> line[1]),
                        RequestHeader::new
                ));
    }

    public String get(String key) {
        return items.get(key);
    }
}
