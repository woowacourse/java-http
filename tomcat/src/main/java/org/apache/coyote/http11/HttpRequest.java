package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);
    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final HttpHeaders headers;
    private final HttpMethod method;
    private final HttpRequestURI requestURI;
    private final String protocol;
    private final Map<String, String> body;

    public HttpRequest(
        final HttpHeaders headers,
        final HttpMethod httpMethod,
        final HttpRequestURI requestURI,
        final String protocol,
        final Map<String, String> body) {
        this.headers = headers;
        this.method = httpMethod;
        this.requestURI = requestURI;
        this.protocol = protocol;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) {
        try {
            final String requestLine = bufferedReader.readLine();
            final var requestLineTokens = requestLine.split(" ");
            final String method = requestLineTokens[0];
            final String uri = requestLineTokens[1];
            final String protocol = requestLineTokens[2];
            final HttpHeaders headers = HttpHeaders.from(bufferedReader);
            final Map<String, String> body = parseBody(headers, bufferedReader);

            LOGGER.info("method: {}, uri: {}, protocol: {}", method, uri, protocol);

            return new HttpRequest(headers, HttpMethod.of(method), HttpRequestURI.from(uri), protocol, body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Map<String, String> parseBody(final HttpHeaders headers, final BufferedReader bufferedReader)
        throws IOException {
        final boolean isURLEncoded = headers.containsHeaderNameAndValue(
            HttpHeaderName.CONTENT_TYPE,
            APPLICATION_X_WWW_FORM_URLENCODED
        );
        final int contentLength = headers.getContentLength();
        if (contentLength > 0 && isURLEncoded) {
            final char[] rowBody = new char[contentLength];
            bufferedReader.read(rowBody, 0, contentLength);
            final String body = new String(rowBody);

            return X_WWW_Form_UrlEncodedDecoder.parse(body);
        }
        return new HashMap<>();
    }

    public boolean containsAccept(final String contentType) {
        return headers.containsHeaderNameAndValue(HttpHeaderName.ACCEPT, contentType);
    }

    public boolean hasQueryString() {
        return requestURI.hasQueryString();
    }

    public boolean containsContentType(final String value) {
        return headers.containsHeaderNameAndValue(HttpHeaderName.CONTENT_TYPE, value);
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

    public HttpMethod getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public HttpRequestURI getRequestURI() {
        return requestURI;
    }

    public Map<String, String> getBody() {
        return new HashMap<>(body);
    }
}
