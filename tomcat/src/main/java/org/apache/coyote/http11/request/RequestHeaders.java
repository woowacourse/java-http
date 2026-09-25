package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RequestHeaders {

    private final Map<String, String> headers;

    private RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(final List<String> headerLines) {
        final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (final String line : headerLines) {
            putHeader(headers, line);
        }
        return new RequestHeaders(headers);
    }

    private static void putHeader(final Map<String, String> headers, final String line) {
        final int idx = line.indexOf(":");
        if (idx == -1) {
            throw new HttpRequestParseException("헤더 형식이 잘못되었습니다: " + line);
        }
        headers.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public int getContentLength() {
        final String value = headers.get("Content-Length");
        if (value == null) {
            return 0;
        }
        return parseContentLength(value);
    }

    private int parseContentLength(final String value) {
        try {
            final int length = Integer.parseInt(value);
            if (length < 0) {
                throw new HttpRequestParseException("Content-Length가 음수입니다: " + value);
            }
            return length;
        } catch (NumberFormatException e) {
            throw new HttpRequestParseException("Content-Length 형식이 잘못되었습니다: " + value);
        }
    }
}
