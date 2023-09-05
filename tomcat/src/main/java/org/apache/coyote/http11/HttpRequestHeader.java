package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpRequestHeader {
    private static final String HEADER_SEPARATOR = ": ";
    private static final String QUERY_START_SYMBOL = "\\?";
    private final HttpMethod method;
    private final String requestUri;
    private final String path;
    private final String protocol;
    private final QueryString queryString;
    private final Map<String, String> headers = new HashMap<>();

    public HttpRequestHeader(final List<String> request) {
        final String[] firstLine = request.get(0).split(" ");
        this.method = HttpMethod.of(firstLine[0]);
        this.requestUri = firstLine[1];
        this.protocol = firstLine[2];
        final List<String> requestTokens = List.of(requestUri.split(QUERY_START_SYMBOL));
        this.path = requestTokens.get(0);
        this.queryString = QueryString.of(requestTokens);
        request.stream()
                .skip(1)
                .takeWhile(line -> !line.isEmpty())
                .map(line -> line.split(HEADER_SEPARATOR))
                .forEach(line -> headers.put(line[0], line[1]));
    }

    public boolean hasCookie() {
        return headers.containsKey("Cookie");
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

    public Optional<Cookie> getJsessionCookie() {
        return getCookies().stream()
                .filter(cookie -> cookie.getName().equals("JSESSIONID"))
                .findFirst();
    }

    public List<Cookie> getCookies() {
        return CookieParser.parse(headers.getOrDefault("Cookie", ""));
    }
}
