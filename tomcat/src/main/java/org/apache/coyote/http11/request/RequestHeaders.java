package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestHeaders {

    private final Map<String, Object> headerMap;

    private RequestHeaders(final Map<String, Object> headerMap) {
        this.headerMap = headerMap;
    }

    public static RequestHeaders from(final BufferedReader br) {
        Map<String, Object> headerMap = br.lines()
                .takeWhile(line -> !line.equals("") && Objects.nonNull(line))
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));

        return new RequestHeaders(headerMap);
    }
}
