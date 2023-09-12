package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.Map;

public class HttpHeaderFactory {

    private HttpHeaderFactory() {
    }

    public static HttpHeaders createHttpHeaders(final BufferedReader bufferedReader) {
        final Map<HttpHeaderName, String> values = parseHeaders(bufferedReader);

        return new HttpHeaders(values);
    }

    private static Map<HttpHeaderName, String> parseHeaders(final BufferedReader bufferedReader) {
        final Map<HttpHeaderName, String> headers = new EnumMap<>(HttpHeaderName.class);

        try {
            String headerLine;
            while (!"".equals(headerLine = bufferedReader.readLine())) {
                final String[] rowHeaderData = headerLine.split(": ");
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
}
