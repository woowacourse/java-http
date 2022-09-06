package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.COOKIE;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.coyote.http11.exception.badrequest.NotExistHeaderException;

public class HttpHeaders {

    private static final String HTTP_HEADER_DELIMITER = ": ";
    private static final String NONE_HEADER = "";

    private final Map<HttpHeader, String> headers;

    public HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public HttpHeaders(final List<String> headers) {
        this.headers = parseHeaders(headers);
    }

    private Map<HttpHeader, String> parseHeaders(final List<String> headers) {
        return headers.stream()
                .filter(header -> !NONE_HEADER.equals(header))
                .map(header -> header.split(HTTP_HEADER_DELIMITER))
                .collect(Collectors.toMap(
                        headerValues -> HttpHeader.of(headerValues[0]),
                        headerValues -> removeSpaceInHeaderValue(headerValues[1]),
                        (x, y) -> y,
                        LinkedHashMap::new)
                );
    }

    private String removeSpaceInHeaderValue(final String headerValue) {
        final int length = headerValue.length();
        if (headerValue.endsWith(" ")) {
            return headerValue.substring(0, length - 1);
        }
        return headerValue;
    }

    public HttpHeaders addHeader(final HttpHeader header, final String value) {
        headers.put(header, value);
        return this;
    }

    public HttpHeaders addHeader(final HttpHeader header, final int value) {
        headers.put(header, String.valueOf(value));
        return this;
    }

    public String encodingToString() {
        final List<String> headers = new ArrayList<>();
        for (Entry<HttpHeader, String> entry : this.headers.entrySet()) {
            final String join = String.join(HTTP_HEADER_DELIMITER, entry.getKey().getValue(), entry.getValue()) + " ";
            headers.add(join);
        }
        return String.join("\r\n", headers);
    }

    public String getValue(final HttpHeader httpHeader) {
        if (headers.isEmpty() || !headers.containsKey(httpHeader)) {
            throw new NotExistHeaderException();
        }
        return this.headers.get(httpHeader);
    }

    public HttpCookie getHttpCookie() {
        if (headers.isEmpty() || !headers.containsKey(COOKIE)) {
            throw new NotExistHeaderException();
        }
        return new HttpCookie(this.headers.get(COOKIE));
    }
}
