package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;

    private HttpRequest(final RequestLine requestLine, final Map<String, String> headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public static HttpRequest from(final String firstLine, final List<String> values) {
        final RequestLine requestLine = RequestLine.of(firstLine);

        final Map<String, String> headers = new HashMap<>();

        for (String value : values) {
            if ("".equals(value)) {
                break;
            }
            final String[] header = value.split(": ");
            headers.put(header[0], header[1]);
        }
        return new HttpRequest(requestLine, headers);
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
