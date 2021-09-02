package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {

    private static final int NONE_QUERY = -1;
    private static final int URL_LOCATION = 1;

    private final Map<String, String> headerLines;
    private final HttpCookies httpCookies;

    public HttpRequestHeader(Map<String, String> headerLines, HttpCookies httpCookies) {
        this.headerLines = headerLines;
        this.httpCookies = httpCookies;
    }

    public static HttpRequestHeader from(Map<String, String> headerLines) {
        if (headerLines.containsKey(HttpHeaderType.COOKIE.value())) {
            final HttpCookies httpCookies = HttpCookies.from(headerLines);
            headerLines.remove(HttpHeaderType.COOKIE.value());
            return new HttpRequestHeader(headerLines, httpCookies);
        }

        final Map<String, String> cookies = new HashMap<>();
        HttpCookies httpCookies = new HttpCookies(cookies);
        return new HttpRequestHeader(headerLines, httpCookies);
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
        final String rawURL = splitFirstLine[URL_LOCATION];
        String[] splitURL = rawURL.split("\\.");
        return splitURL[splitURL.length - 1];
    }

    public int contentLength() {
        return Integer.parseInt(headerLines.get(HttpHeaderType.CONTENT_LENGTH.value()));
    }

    public HttpCookies cookie() {
        return this.httpCookies;
    }
}
