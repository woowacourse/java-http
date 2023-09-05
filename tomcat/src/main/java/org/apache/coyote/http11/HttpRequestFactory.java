package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestFactory {

    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public static HttpRequest createHttpRequest(final BufferedReader bufferedReader) {
        try {
            final String requestLine = bufferedReader.readLine();
            final var requestLineTokens = requestLine.split(" ");
            final String method = requestLineTokens[0];
            final String uri = requestLineTokens[1];
            final String protocol = requestLineTokens[2];
            final HttpHeaders headers = HttpHeaders.from(bufferedReader);
            final Map<String, String> body = parseBody(headers, bufferedReader);

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
}
