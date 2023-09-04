package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {
    Map<String, Object> headers;

    public RequestHeaders(final Map<String, Object> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(final BufferedReader br) {
        Map<String, Object> headers = br.lines()
                .takeWhile(line -> !line.equals(""))
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));

        return new RequestHeaders(headers);
    }

    boolean hasContentType() {
        return headers.containsKey("Content-Type");
    }

    public Object get(final String headerKey) {
        return headers.get(headerKey);
    }
}
