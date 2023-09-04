package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpHeaders {

    private static final String CRLF = "\r\n";

    private final Map<HttpHeaderName, String> headers;

    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    public HttpHeaders(final Map<HttpHeaderName, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders from(final BufferedReader bufferedReader) {
        final Map<HttpHeaderName, String> headers = parseHeaders(bufferedReader);

        return new HttpHeaders(headers);
    }

    private static Map<HttpHeaderName, String> parseHeaders(final BufferedReader bufferedReader) {
        final Map<HttpHeaderName, String> headers = new HashMap<>();

        try {
            String headerLine;
            while (!"".equals(headerLine = bufferedReader.readLine())) {
                final String[] rowHeaderData = headerLine.split(":");
                final HttpHeaderName headerName = HttpHeaderName.getHeaderName(rowHeaderData[0].trim());
                final String headerContent = rowHeaderData[1].trim();

                headers.put(headerName, headerContent);
            }
            parseToCookie(headers);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return headers;
    }

    private static void parseToCookie(final Map<HttpHeaderName, String> headers) {
        headers.computeIfPresent(HttpHeaderName.COOKIE, (key, value) -> HttpCookie.from(value).toString());
    }

    public static HttpHeaders makeHttpResponseHeaders(final String contentType, final String body) {
        final Map<HttpHeaderName, String> headers = Map.of(
            HttpHeaderName.CONTENT_TYPE, contentType,
            HttpHeaderName.CONTENT_LENGTH, String.valueOf(body.getBytes().length)
        );

        return new HttpHeaders(headers);
    }

    public boolean containsHeader(final HttpHeaderName headerName) {
        return headers.containsKey(headerName);
    }

    public boolean containsHeaderNameAndValue(final HttpHeaderName headerName, final String headerValue) {
        return containsHeader(headerName) && getHeaderValue(headerName).contains(headerValue);
    }

    public void addHeader(final HttpHeaderName headerName, final String value) {
        headers.put(headerName, value);
    }

    public void addHeader(final HttpHeaders httpHeaders) {
        headers.putAll(httpHeaders.headers);
    }

    public int getContentLength() {
        if (headers.containsKey(HttpHeaderName.CONTENT_LENGTH)) {
            return Integer.parseInt(headers.get(HttpHeaderName.CONTENT_LENGTH));
        }
        return 0;
    }

    public HttpCookie getCookie() {
        if (headers.containsKey(HttpHeaderName.COOKIE)) {
            return HttpCookie.from(headers.get(HttpHeaderName.COOKIE));
        }
        throw new IllegalStateException("쿠키가 존재하지 않습니다.");
    }

    public String getHeaderValue(final HttpHeaderName headerName) {
        return headers.get(headerName);
    }

    @Override
    public String toString() {
        final List<Entry<HttpHeaderName, String>> entrySet = headers.entrySet().stream()
            .sorted(Entry.comparingByKey())
            .collect(Collectors.toList());
        final StringBuilder stringBuilder = new StringBuilder();

        for (final Entry<HttpHeaderName, String> entry : entrySet) {
            stringBuilder.append(entry.getKey().getValue())
                .append(": ")
                .append(entry.getValue())
                .append(CRLF);
        }

        return stringBuilder.toString();
    }
}
