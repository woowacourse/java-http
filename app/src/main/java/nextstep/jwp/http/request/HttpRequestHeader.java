package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {
    private static final int NONE_QUERY = -1;

    private final Map<String, String> headerLines;
    private final HttpCookie httpCookie;

    public HttpRequestHeader(Map<String, String> headerLines, HttpCookie httpCookie) {
        this.headerLines = headerLines;
        this.httpCookie = httpCookie;
    }

    public static HttpRequestHeader from(Map<String, String> headerLines) {
        if (headerLines.containsKey(HttpHeaderType.COOKIE.value())) {
            final HttpCookie httpCookie = HttpCookie.from(headerLines);
            headerLines.remove(HttpHeaderType.COOKIE.value());
            return new HttpRequestHeader(headerLines, httpCookie);
        }

        final Map<String, String> cookies = new HashMap<>();
        HttpCookie httpCookie = new HttpCookie(cookies);
        return new HttpRequestHeader(headerLines, httpCookie);
    }

    public String method() {
        final String firstLine = headerLines.get(HttpHeaderType.START_LINE.value());
        final String[] splitFirstLine = firstLine.split(" ");
        return splitFirstLine[0];
    }

    public String getRequestURLWithoutQuery() {
        final String firstLine = headerLines.get(HttpHeaderType.START_LINE.value());
        final String[] splitFirstLine = firstLine.split(" ");
        final String rawURL = splitFirstLine[1];
        final int index = rawURL.indexOf("?");

        if (index == NONE_QUERY) {
            return rawURL;
        }
        return rawURL.substring(0, index);
    }

    public boolean isResource() {
        final String firstLine = headerLines.get(HttpHeaderType.START_LINE.value());
        final String[] splitFirstLine = firstLine.split(" ");
        final String rawURL = splitFirstLine[1];
        String[] splitURL = rawURL.split("\\.");
        return splitURL.length != 1;
    }

    public String resourceType() {
        final String firstLine = headerLines.get(HttpHeaderType.START_LINE.value());
        final String[] splitFirstLine = firstLine.split(" ");
        final String rawURL = splitFirstLine[1];
        String[] splitURL = rawURL.split("\\.");
        return splitURL[splitURL.length - 1];
    }

    public int contentLength() {
        return Integer.parseInt(headerLines.get(HttpHeaderType.CONTENT_LENGTH.value()));
    }

    public HttpCookie cookie() {
        return this.httpCookie;
    }
}
