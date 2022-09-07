package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpRequestHeaders {

    private static final Pattern HEADER_PATTERN = Pattern.compile("(?<info>[a-zA-Z\\- ]+): ?(?<value>.+)");
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private final Map<String, String> headers;

    private HttpRequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpRequestHeaders parse(final List<String> headers) {
        return new HttpRequestHeaders(headers.stream()
                .map(HEADER_PATTERN::matcher)
                .filter(Matcher::find)
                .collect(Collectors.toMap(
                        matcher -> matcher.group("info"),
                        matcher -> matcher.group("value")
                )));
    }

    public boolean hasRequestBody() {
        return headers.containsKey(CONTENT_LENGTH);
    }

    public String getJSessionId() {
        return getCookiesData().getJSessionId();
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }

    public HttpCookie getCookiesData() {
        return HttpCookie.from(headers.get(COOKIE));
    }
}
