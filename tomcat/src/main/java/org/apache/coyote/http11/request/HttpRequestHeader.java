package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestHeader {
    private static final String PAIR_DELIMITER = ": ";

    private final Map<String, String> httpRequestHeaders;

    private HttpRequestHeader(final Map<String, String> httpRequestHeaders) {
        this.httpRequestHeaders = httpRequestHeaders;
    }

    public static HttpRequestHeader from(final List<String> httpRequestHeader) {
        return new HttpRequestHeader(
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
        return httpRequestHeaders.containsKey("Cookie");
    }

    public Map<String, String> getHttpRequestHeaders() {
        return httpRequestHeaders;
    }

    public String getContentLength() {
        return httpRequestHeaders.get("Content-Length");
    }

    public String getCookie() {
        return httpRequestHeaders.get("Cookie");
    }
}
