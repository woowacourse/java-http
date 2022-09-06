package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import utils.StringSplitter;

public class RequestHeader {

    private final Map<String, String> headers;

    public RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader parse(final List<String> headerLines) {
        final Map<String, String> headers = parseHeaders(headerLines);

        return new RequestHeader(headers);
    }

    private static Map<String, String> parseHeaders(final List<String> lines) {
        final String headerDelimiter = ":";
        final Map<String, String> headers = StringSplitter.getPairs(headerDelimiter, lines);
        return headers.entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        entry -> trim(entry.getKey()),
                        entry -> trim(entry.getValue())
                ));
    }

    private static String trim(final String text) {
        return text.trim();
    }
}
