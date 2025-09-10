package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.coyote.http11.cookie.HttpCookie;

public final class HttpRequest {

    public static final String JSESSIONID = "JSESSIONID";
    private static final String EQUAL = "=";
    private static final String COOKIE = "cookie";
    private static final String SEMICOLON = ";";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final HttpCookie httpCookie;
    private final String path;
    private final Map<String, String> queries;
    private final byte[] body;

    public HttpRequest(final RequestLine requestLine,
                       final Map<String, String> headers,
                       final String path,
                       final Map<String, String> queries,
                       final byte[] body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.httpCookie = getHttpCookieFromHeaders();
        this.path = path;
        this.queries = queries;
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
//        headers.remove(key);

        return cookie;
    }

    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }

    public Map<String, String> findCookie() {
        Map<String, String> answer = new HashMap<>();
        String session = headers.get(COOKIE);
        for (String s : session.split(SEMICOLON)) {
            int equalIndex = s.indexOf(EQUAL);
            String key = s.substring(0, equalIndex).trim();
            String value = s.substring(equalIndex + 1).trim();
            answer.put(key, value);
        }
        return answer;
    }

    public boolean hasCookie() {
        return headers.containsKey(COOKIE);
    }

    private Optional<String> getCookieValue() {
        return headers.entrySet()
                .stream()
                .filter(cookie -> COOKIE.equals(cookie.getKey()))
                .map(Entry::getValue)
                .findFirst();
    }

    public Optional<String> findSession() {
        return Optional.ofNullable(headers.get(JSESSIONID));
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public String path() {
        return path;
    }

    public Map<String, String> queries() {
        return queries;
    }

    public byte[] body() {
        return body;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }
}
