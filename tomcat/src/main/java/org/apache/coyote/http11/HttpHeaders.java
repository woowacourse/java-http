package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpHeaders {

    private static final String HTTP_HEADER_DELIMITER = ": ";
    private static final String NONE_HEADER = "";

    private final Map<HttpHeader, String> headers;

    public HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public HttpHeaders(final BufferedReader bufferedReader) throws IOException {
        this.headers = extractHeaders(bufferedReader);
    }

    private Map<HttpHeader, String> extractHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<HttpHeader, String> headers = new LinkedHashMap<>();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();
            parseHeader(headers, line);
        }
        return headers;
    }

    private void parseHeader(final Map<HttpHeader, String> headers, final String line) {
        if (!NONE_HEADER.equals(line)) {
            final String[] headerValues = line.split(HTTP_HEADER_DELIMITER);
            final HttpHeader httpHeader = HttpHeader.of(headerValues[0]);
            final String value = headerValues[1];
            headers.put(httpHeader, value);
        }
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
}
