package org.apache.coyote.http11.request;

import static org.apache.coyote.QueryStringParser.parseQueryString;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final Map<String, String> body;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers,
                        final Map<String, String> body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final String firstLine, final List<String> values, final BufferedReader reader)
            throws IOException {
        final RequestLine requestLine = RequestLine.of(firstLine);

        final HttpHeaders httpHeaders = toHttpHeaders(values);

        final Map<String, String> body = new HashMap<>(Collections.EMPTY_MAP);
        if (httpHeaders.hasContentLength()) {
            final int contentLength = Integer.parseInt(httpHeaders.getContentLength());
            final char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            final String requestBody = new String(buffer);
            body.putAll(parseQueryString(requestBody));
        }

        return new HttpRequest(requestLine, httpHeaders, body);
    }

    private static HttpHeaders toHttpHeaders(final List<String> values) {
        final Map<String, String> headers = new HashMap<>();
        for (String value : values) {
            final String[] header = value.split(": ");
            headers.put(header[0], header[1]);
        }
        final HttpHeaders httpHeaders = new HttpHeaders(headers);
        return httpHeaders;
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

    public Map<String, String> getBody() {
        return body;
    }
}
