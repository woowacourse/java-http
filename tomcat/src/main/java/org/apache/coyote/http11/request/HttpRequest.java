package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;


    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = new RequestBody();
    }

    public static HttpRequest from(final BufferedReader bufferedReader) {
        try {
            final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
            final RequestHeaders requestHeaders = RequestHeaders.from(bufferedReader);
            return new HttpRequest(requestLine, requestHeaders);
        } catch (final IOException e) {
            log.error("invalid input", e);
            throw new IllegalArgumentException("올바른 HttpRequest 형식이 아닙니다.");
        }
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public boolean containsQuery() {
        return requestLine.containsQuery();
    }

    public String findQueryValue(final String key) {
        return requestLine.findQueryValue(key);
    }
}
