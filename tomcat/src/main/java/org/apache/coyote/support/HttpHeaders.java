package org.apache.coyote.support;

import java.util.Map;
import java.util.Optional;
import org.apache.coyote.web.session.Cookie;

public class HttpHeaders {

    private static final String CONTENT_LENGTH_DEFAULT_VALUE = "0";
    private static final String COOKIE_DEFAULT_VALUE = "";
    private static final String COOKIE_TEMPLATE = "%s=%s";
    private static final String ADD_COOKIE_TEMPLATE = "%s; %s";

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void put(final String key, final String value) {
        headers.put(key, value);
    }

    public Optional<String> getHeader(final String header) {
        String foundHeader = headers.get(header);
        if (foundHeader == null) {
            return Optional.empty();
        }
        return Optional.of(foundHeader);
    }

    public void setContentLength(final int length) {
        headers.put(HttpHeader.CONTENT_LENGTH.getValue(), String.valueOf(length));
    }

    public void setContentType(final ContentType contentType) {
        headers.put(HttpHeader.CONTENT_TYPE.getValue(), contentType.getValue());
    }

    public String getContentType() {
        return headers.getOrDefault(HttpHeader.CONTENT_TYPE.getValue(), ContentType.STRINGS.getValue());
    }

    public String getContentLength() {
        return headers.getOrDefault(HttpHeader.CONTENT_LENGTH.getValue(), CONTENT_LENGTH_DEFAULT_VALUE);
    }

    public void setLocation(final String url) {
        headers.put(HttpHeader.LOCATION.getValue(), url);
    }

    public void setCookie(final Cookie cookie) {
        headers.put(HttpHeader.SET_COOKIE.getValue(),
                String.format(COOKIE_TEMPLATE, cookie.getKey(), cookie.getValue()));
    }

    public void addCookie(final Cookie cookie) {
        String cookieFormat = String.format(COOKIE_TEMPLATE, cookie.getKey(), cookie.getValue());
        if (headers.containsKey(HttpHeader.SET_COOKIE.getValue())) {
            headers.computeIfPresent(HttpHeader.SET_COOKIE.getValue(),
                    (k, v) -> String.format(ADD_COOKIE_TEMPLATE, v, cookieFormat));
            return;
        }
        setCookie(cookie);
    }

    public String getCookie() {
        return headers.getOrDefault(HttpHeader.COOKIE.getValue(), COOKIE_DEFAULT_VALUE);
    }
}
