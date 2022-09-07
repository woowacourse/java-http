package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers) {
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

        final HttpHeaders httpHeaders = new HttpHeaders(headers);
        return new HttpRequest(requestLine, httpHeaders);
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHeaders() {
        return headers.getValues();
    }
}
