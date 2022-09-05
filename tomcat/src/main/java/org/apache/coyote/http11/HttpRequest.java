package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String url;
    private final Map<HttpHeader, String> headers;

    private HttpRequest(final String method, final String url,
                        final Map<HttpHeader, String> headers) {
        this.method = method;
        this.url = url;
        this.headers = headers;
    }

    public static HttpRequest from(final String requestLine, final List<String> values) {
        final String[] requestValues = requestLine.split(" ");

        final String method = requestValues[0];
        final String uri = requestValues[1];

        final Map<HttpHeader, String> headers = new HashMap<>();

        for (String value : values) {
            if ("".equals(value)) {
                break;
            }
            final String[] header = value.split(": ");
            headers.put(HttpHeader.of(header[0]), header[1]);
        }
        return new HttpRequest(method, uri, headers);
    }

    public String getUrl() {
        return url;
    }
}
