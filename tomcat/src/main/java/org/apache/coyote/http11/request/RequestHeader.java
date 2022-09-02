package org.apache.coyote.http11.request;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import utils.StringSplitter;

public class RequestHeader {

    private final URI uri;
    private final Map<String, String> headers;

    public RequestHeader(final URI uri, final Map<String, String> headers) {
        this.uri = uri;
        this.headers = headers;
    }

    public static RequestHeader parse(final String startLine, final List<String> headerLines) {
        final URI uri = parseUri(startLine);
        final Map<String, String> headers = parseHeaders(headerLines);

        return new RequestHeader(uri, headers);
    }

    private static URI parseUri(final String startLine) {
        final String trimmedStartLine = trim(startLine);

        final String startLineDelimiter = " ";
        final int firstIndex = trimmedStartLine.indexOf(startLineDelimiter);
        final int lastIndex = trimmedStartLine.lastIndexOf(startLineDelimiter);

        final String uri = startLine.substring(firstIndex, lastIndex);

        try {
            return new URI(trim(uri));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URI 형식이 잘못되었습니다.");
        }
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

    public URI getUri() {
        return uri;
    }
}
