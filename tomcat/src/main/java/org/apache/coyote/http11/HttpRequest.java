package org.apache.coyote.http11;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.coyote.http11.cookie.HttpCookie;

public final class HttpRequest {

    private static final String EQUAL = "=";
    private static final String COOKIE = "cookie";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final HttpCookie httpCookie;
    private final String path;
    private final byte[] body;

    public HttpRequest(final RequestLine requestLine,
                       final Map<String, String> headers,
                       final String path,
                       final byte[] body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.httpCookie = getHttpCookieFromHeaders();
        this.path = path;
        this.body = body;
    }

    private HttpCookie getHttpCookieFromHeaders() {
        HttpCookie cookie = new HttpCookie();
        Optional<String> optionalCookie = getCookieValue();
        if (optionalCookie.isEmpty()) {
            return cookie;
        }
        String cookieValue = optionalCookie.get();
        int equalIndex = cookieValue.indexOf(EQUAL);
        if (equalIndex < 0) {
            return cookie;
        }
        String key = cookieValue.substring(0, equalIndex);
        String value = cookieValue.substring(equalIndex + 1);
        cookie.addCookie(key, value);

        return cookie;
    }

    private Optional<String> getCookieValue() {
        return headers.entrySet()
                .stream()
                .filter(cookie -> COOKIE.equals(cookie.getKey()))
                .map(Entry::getValue)
                .findFirst();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getPath() {
        return path;
    }

    public byte[] body() {
        return body;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public HttpMethod getMethod() {
        return requestLine.method();
    }
}
