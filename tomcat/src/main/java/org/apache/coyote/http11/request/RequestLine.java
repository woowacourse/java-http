package org.apache.coyote.http11.request;

import java.net.URI;
import java.net.URISyntaxException;

import utils.StringSplitter;

public class RequestLine {

    private static final String DELIMITER = " ";

    private final RequestMethod method;
    private final Uri uri;

    private RequestLine(final RequestMethod method, final Uri uri) {
        this.method = method;
        this.uri = uri;
    }

    public static RequestLine parse(final String startLine) {
        final String trimmedStartLine = startLine.trim();

        final RequestMethod method = parseMethod(trimmedStartLine);
        final Uri uri = parseUri(trimmedStartLine);

        return new RequestLine(method, uri);
    }

    private static RequestMethod parseMethod(final String startLine) {
        return RequestMethod.valueOf(StringSplitter.getFirst(DELIMITER, startLine));
    }

    private static Uri parseUri(final String startLine) {
        final int firstIndex = startLine.indexOf(DELIMITER);
        final int lastIndex = startLine.lastIndexOf(DELIMITER);

        final String uri = startLine.substring(firstIndex, lastIndex);

        try {
            return Uri.parse(new URI(uri.trim()));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URI 형식이 잘못되었습니다.");
        }
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return uri.getPath();
    }

    public Params getParams() {
        return uri.getParams();
    }
}
