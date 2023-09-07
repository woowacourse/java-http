package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestHeaders {
    private static final String PAIR_DELIMITER = ": ";

    private final Map<String, String> headers;

    private HttpRequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeaders from(final List<String> httpRequestHeader) {
        return new HttpRequestHeaders(
                httpRequestHeader
                        .stream()
                        .map(line -> line.split(PAIR_DELIMITER))
                        .collect(Collectors.toMap(
                                line -> line[0],
                                line -> line[1])
                        )
        );
    }

    public boolean hasCookie() {
        return headers.containsKey("Cookie");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getContentLength() {
        return headers.get("Content-Length");
    }

    public String getCookie() {
        return headers.get("Cookie");
    }
}
