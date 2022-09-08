package nextstep.jwp.http.common;

import java.util.Map;

public class HttpHeaders {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String LOCATION = "Location";
    public static final String COOKIE = "Cookie";
    public static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE_FORMAT = "%s=%s";
    private static final String ADDITIONAL_COOKIE_FORMAT = "%s; %s";

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public void add(String key, String value) {
        headers.put(key, value);
    }

    public void addSetCookie(final HttpCookie httpCookie) {
        String addCookie = String.format(COOKIE_FORMAT, httpCookie.getKey(), httpCookie.getValue());
        if (headers.containsKey(SET_COOKIE)) {
            headers.computeIfPresent(SET_COOKIE, (k, v) -> String.format(ADDITIONAL_COOKIE_FORMAT, v, addCookie));
            return;
        }
        addInitialSetCookie(httpCookie);
    }

    private void addInitialSetCookie(final HttpCookie httpCookie) {
        headers.put(SET_COOKIE, String.format(COOKIE_FORMAT, httpCookie.getKey(), httpCookie.getValue()));
    }

    public int getContentLength() {
        String contentLength = headers.getOrDefault(CONTENT_LENGTH, "0");
        return Integer.parseInt(contentLength);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public boolean isExistSetCookie() {
        boolean exist = headers.containsKey(SET_COOKIE);
        return exist;
    }

    public boolean isExistCookie() {
        boolean exist = headers.containsKey(COOKIE);
        return exist;
    }
}
