package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class HttpRequestHeader {

    private Map<String, String> headers = new HashMap<>();

    private HttpRequestHeader(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
    }

    public static HttpRequestHeader from(String headers) {
        return Arrays.stream(headers.split("\r\n"))
                .map(header -> header.split(": "))
                .collect(collectingAndThen(
                        toMap(header -> header[0], header -> header[1]),
                        HttpRequestHeader::new
                ));
    }

    public String find(String header) {
        return headers.get(header);
    }

    public String contentLength() {
        return find("Content-Length");
    }
}
