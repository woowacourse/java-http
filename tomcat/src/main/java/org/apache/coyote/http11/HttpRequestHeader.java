package org.apache.coyote.http11;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestHeader {
    private static final String HEADER_SEPARATOR = ": ";
    private static final String QUERY_START_SYMBOL = "\\?";

    private final HttpMethod method;
    private final String requestUri;
    private final String path;
    private final String protocol;
    private final QueryString queryString;
    private final Map<String, String> headers;
    private final Cookies cookies;

    public HttpRequestHeader(final List<String> request) {
        final String[] firstLine = request.get(0).split(" ");
        this.method = HttpMethod.of(firstLine[0]);
        this.requestUri = firstLine[1];
        this.protocol = firstLine[2];
        final List<String> requestTokens = List.of(requestUri.split(QUERY_START_SYMBOL));
        this.path = requestTokens.get(0);
        this.queryString = QueryString.of(requestTokens);
        this.headers = parseHeaders(request);
        this.cookies = parseCookies();
    }

    private Cookies parseCookies() {
        if (headers.containsKey("Cookie")) {
            return Cookies.parse(headers.get("Cookie"));
        }
        return Cookies.empty();
    }

    private Map<String, String> parseHeaders(final List<String> request) {
        return request.stream()
                .skip(1)
                .takeWhile(line -> !line.isEmpty())
                .map(line -> line.split(HEADER_SEPARATOR))
                .collect(Collectors.toMap(header -> header[0], header -> header[1]));
    }

    public boolean hasCookie() {
        return headers.containsKey("Cookie");
    }

    public boolean containsCookieKey(final String key) {
        return cookies.containsKey(key);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", requestUri='" + requestUri + '\'' +
                ", path='" + path + '\'' +
                ", protocol='" + protocol + '\'' +
                ", queryString=" + queryString +
                ", headers=" + headers +
                '}';
    }

    public HttpMethod getMethod() {
        return method;
    }

    public boolean isGet() {
        return method == HttpMethod.GET;
    }

    public boolean isPost() {
        return method == HttpMethod.POST;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getPath() {
        return path;
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }

    public ContentType getContentType() {
        return ContentType.of(headers.getOrDefault("Content-Type", ""));
    }
}
