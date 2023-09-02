package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);

    private final HttpHeaders headers;
    private final String method;
    private final HttpRequestURI requestURI;
    private final String protocol;

    public HttpRequest(
        final HttpHeaders headers,
        final String method,
        final HttpRequestURI requestURI,
        final String protocol
    ) {
        this.headers = headers;
        this.method = method;
        this.requestURI = requestURI;
        this.protocol = protocol;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) {
        try {
            final String requestLine = bufferedReader.readLine();
            final var requestLineTokens = requestLine.split(" ");
            final String method = requestLineTokens[0];
            final String uri = requestLineTokens[1];
            final String protocol = requestLineTokens[2];
            final HttpHeaders headers = HttpHeaders.from(bufferedReader);

            LOGGER.info("method: {}, uri: {}, protocol: {}", method, uri, protocol);

            return new HttpRequest(headers, method, HttpRequestURI.from(uri), protocol);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public boolean containsAccept(final String contentType) {
        return headers.containsHeaderNameAndValue(HttpHeaderName.ACCEPT, contentType);
    }

    public String getPath() {
        return requestURI.getPath();
    }

    public QueryStrings getQueryStrings() {
        return requestURI.getQueryStrings();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public boolean hasQueryString() {
        return requestURI.hasQueryString();
    }
}
