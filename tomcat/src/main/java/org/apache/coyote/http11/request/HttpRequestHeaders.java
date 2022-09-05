package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpRequestHeaders {

    private static final Pattern HEADER_PATTERN = Pattern.compile("(?<info>[a-zA-Z\\- ]+): ?(?<value>.+)");

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
        return headers.containsKey("Content-Length");
    }

    public boolean hasJSessionId() {
        return getCookiesData().hasJSessionId();
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
    }

    public HttpCookie getCookiesData() {
        return HttpCookie.from(headers.get("Cookie"));
    }
}
