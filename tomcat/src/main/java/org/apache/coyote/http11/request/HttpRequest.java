package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;


    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders,
                        final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) {
        try {
            final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
            final RequestHeaders requestHeaders = RequestHeaders.from(bufferedReader);
            final RequestBody requestBody = RequestBody.of(bufferedReader, requestHeaders);
            return new HttpRequest(requestLine, requestHeaders, requestBody);
        } catch (final IOException e) {
            log.error("invalid input", e);
            throw new IllegalArgumentException("올바른 HttpRequest 형식이 아닙니다.");
        }
    }

    public Optional<String> findCookie(final String name) {
        return requestHeaders.findCookie(name);
    }

    public boolean isPostMethod() {
        return requestLine.isPostMethod();
    }

    public String getUri() {
        return requestLine.getPath();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public Map<String, String> parseApplicationFormData() {
        return requestBody.parseApplicationFormData();
    }
}
