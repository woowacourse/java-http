package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);
    private static final String INDEX_HTML_FILE_PATH = "index.html";
    private static final String INDEX_PATH = "/";

    private final HttpHeaders headers;
    private final String method;
    private final String path;
    private final String protocol;

    private HttpRequest(
        final String method,
        final String path,
        final String protocol,
        final HttpHeaders headers
    ) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.headers = headers;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) {
        try {
            final String requestLine = bufferedReader.readLine();
            final var requestLineTokens = requestLine.split(" ");
            final String method = requestLineTokens[0];
            final String path = requestLineTokens[1];
            final String protocol = requestLineTokens[2];
            final HttpHeaders headers = HttpHeaders.from(bufferedReader);

            LOGGER.info("method: {}, path: {}, protocol: {}", method, path, protocol);

            return new HttpRequest(method, path, protocol, headers);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public boolean containsAccept(final String contentType) {
        return headers.containsHeaderNameAndValue(HttpHeaderName.ACCEPT, contentType);
    }

    public String getPath() {
        if (INDEX_PATH.equals(path)) {
            return INDEX_HTML_FILE_PATH;
        }
        return path;
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
}
