package org.apache.coyote.http11.response;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

@Getter
public class HttpResponseHeaders {

    private Map<String, String> headers;

    private HttpResponseHeaders(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
    }

    private HttpResponseHeaders() {
        this(new HashMap<>());
    }

    public static HttpResponseHeaders from(String headers) {
        return Arrays.stream(headers.split("\r\n"))
                .map(header -> header.split(": "))
                .collect(collectingAndThen(
                        toMap(header -> header[0], header -> header[1]),
                        HttpResponseHeaders::new
                ));
    }
}
