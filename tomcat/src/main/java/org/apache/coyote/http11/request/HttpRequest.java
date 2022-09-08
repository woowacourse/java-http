package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.common.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;


    private HttpRequest(final RequestLine requestLine, final Headers headers,
                        final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) {
        try {
            final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
            final Headers headers = Headers.from(bufferedReader);
            final RequestBody requestBody = RequestBody.of(bufferedReader, headers);
            return new HttpRequest(requestLine, headers, requestBody);
        } catch (final IOException e) {
            log.error("invalid input", e);
            throw new IllegalArgumentException("올바른 HttpRequest 형식이 아닙니다.");
        }
    }

    public Optional<String> findCookie(final String name) {
        return headers.findCookie(name);
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public String getUri() {
        return requestLine.getPath();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getRequestHeaders() {
        return headers;
    }

    public Map<String, String> parseApplicationFormData() {
        return requestBody.parseApplicationFormData();
    }
}
