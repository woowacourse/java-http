package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {

    private static final String COOKIE_HEADER = "Cookie";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String JSESSIONID_NAME = "JSESSIONID";
    public static final String HOST = "Host";

    private final Map<String, String> headers;
    private final HttpCookie cookie;

    private RequestHeaders(Map<String, String> headers, HttpCookie cookie) {
        this.headers = headers;
        this.cookie = cookie;
    }

    public static RequestHeaders of(List<String> lines) {
        Map<String, String> headers = lines.stream()
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(splitLine -> splitLine[0], splitLine -> splitLine[1].trim()));
        HttpCookie cookie = createCookie(headers);
        return new RequestHeaders(headers, cookie);
    }

    private static HttpCookie createCookie(Map<String, String> headers) {
        if (headers.containsKey(COOKIE_HEADER)) {
            return HttpCookie.of(headers.get(COOKIE_HEADER));
        }
        return HttpCookie.emptyCookie();
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH_HEADER, String.valueOf(0)));
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public String getJSessionId() {
        return this.cookie.get(JSESSIONID_NAME);
    }

    public boolean doesNeedToSetJSessionIdCookie() {
        return this.cookie.doesNeedToSetJSessionIdCookie();
    }
}
